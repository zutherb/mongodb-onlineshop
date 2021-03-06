package com.comsysto.shop.ui.navigation;

import com.comsysto.shop.service.authentication.api.AuthenticationService;
import com.comsysto.shop.repository.product.model.ProductType;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author zutherb
 */
@Component("navigationProvider")
public class NavigationProviderImpl implements NavigationProvider {

    private static final String[] PACKAGE_SCAN_PATH = {"com.comsysto.shop.ui.page"};

    private static final Set<Class<? extends Page>> PAGES_WITH_NAVIGATION_ANNOTATION;
    private static final Set<Class<? extends Page>> PAGES_WITH_ENUM_NAVIGATION_ANNOTATION;

    static {
        PAGES_WITH_NAVIGATION_ANNOTATION = NavigationPageUtil.getAnnotatedWicketPage(PACKAGE_SCAN_PATH[0],
                NavigationItem.class);
        PAGES_WITH_ENUM_NAVIGATION_ANNOTATION = NavigationPageUtil.getAnnotatedWicketPage(PACKAGE_SCAN_PATH[0],
                EnumProductTypeNavigationItem.class);
    }

    private ApplicationContext applicationContext;
    private AuthenticationService authenticationService;

    @Autowired
    public NavigationProviderImpl(ApplicationContext applicationContext,
                                  AuthenticationService authenticationService) {
        this.applicationContext = applicationContext;
        this.authenticationService = authenticationService;
    }

    @Override
    public Navigation getNavigation() {
        Navigation navigation = new Navigation();
        for (Class<?> aClass : PAGES_WITH_NAVIGATION_ANNOTATION) {
            processNavigationItem(navigation, aClass);
        }
        for (Class<?> aClass : PAGES_WITH_ENUM_NAVIGATION_ANNOTATION) {
            processEnumNavigationItem(navigation, aClass);
        }
        return navigation;
    }

    private void processNavigationItem(Navigation navigation, Class<?> aClass) {
        NavigationItem annotation = aClass.getAnnotation(NavigationItem.class);
        NavigationGroup navigationGroup = getNavigationGroup(navigation, annotation.group());
        Class<? extends Page> aClassAsSubClass = aClass.asSubclass(Page.class);

        navigationGroup.getNavigationEntries().add(new NavigationEntry(annotation.name(), annotation.sortOrder(),
                aClassAsSubClass, isVisible(annotation.visible(), aClass)));
    }

    private void processEnumNavigationItem(Navigation navigation, Class<?> aClass) {
        EnumProductTypeNavigationItem annotation = aClass.getAnnotation(EnumProductTypeNavigationItem.class);
        NavigationGroup navigationGroup = getNavigationGroup(navigation, annotation.group());
        Class<? extends Page> aClassAsSubClass = aClass.asSubclass(Page.class);

        for (ProductType anEnum : annotation.enumClazz().getEnumConstants()) {
            ProductType productType = anEnum;
            PageParameters pageParameters = new PageParameters().set("type", productType.getUrlname());

            navigationGroup.getNavigationEntries().add(new NavigationEntry(productType.getName(), annotation.sortOrder(),
                    aClassAsSubClass, pageParameters, isVisible(annotation.visible(), aClass)));
        }
    }


    private NavigationGroup getNavigationGroup(Navigation navigation, String groupName) {
        if (navigation.getNavigationGroup(groupName) == null) {
            navigation.getNavigationGroups().add(new NavigationGroup(groupName));
        }
        return navigation.getNavigationGroup(groupName);
    }

    private boolean isVisible(String visible, Class<?> aClass) {
        if (aClass.isAnnotationPresent(Secured.class)) {
            Secured annotation = aClass.getAnnotation(Secured.class);
            return isVisible(visible) && authenticationService.isAuthorized(annotation.value());
        }
        return isVisible(visible);
    }

    private boolean isVisible(String visible) {
        if (StringUtils.isNotEmpty(visible)) {
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setBeanResolver(new ApplicationContextBeanResolver());
            return parser.parseExpression(visible).getValue(context, Boolean.class);
        }
        return true;
    }

    private class ApplicationContextBeanResolver implements BeanResolver {
        public Object resolve(EvaluationContext context, String beanname) throws AccessException {
            return applicationContext.getBean(beanname);
        }
    }

    @Override
    public void setClassPathToScan(String classPathToScan) {
        PACKAGE_SCAN_PATH[0] = classPathToScan;
    }
}
