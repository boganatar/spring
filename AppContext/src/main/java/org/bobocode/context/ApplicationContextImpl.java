package org.bobocode.context;

import org.bobocode.annotation.Bean;
import org.bobocode.exception.NoSuchBeanException;
import org.bobocode.exception.NoUniqueBeanException;
import org.reflections.Reflections;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ApplicationContextImpl implements ApplicationContext{
    private final Map<String,Object> beansMap = new ConcurrentHashMap<>();
    public ApplicationContextImpl(String packageName) {
        init(packageName);
    }

    private void init(String packageName) {
        Reflections reflections = new Reflections(packageName);
        reflections.getTypesAnnotatedWith(Bean.class).forEach(this::putBean);
    }
     private void putBean(Class<?> bean) {
        try {
            var object = bean.getDeclaredConstructor().newInstance();
            var name = resolveName(bean);
            beansMap.put(name, object);
        } catch (Exception e) {
            System.out.println("Exception during bean creation: "+ e);
        }
     }

     private String resolveName(Class<?> bean) {
         var name = bean.getAnnotation(Bean.class).value();
         var simpleName = bean.getSimpleName();
         return name.isEmpty() ? (simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1)) : name;
     }

    public <T> T getBean(Class<T> beanType) {
        Map<String, T> beans = getAllBeans(beanType);
        if (beans.size() > 1) throw new NoUniqueBeanException("More than 1 identical bean found.");
        return beans.values().stream()
                .findAny()
                .map(beanType::cast)
                .orElseThrow(() -> new NoSuchBeanException("No such bean."));
    }

    public <T> T getBean(String name, Class<T> beanType) {
        return beansMap.entrySet().stream()
                .filter(e -> name.equals(e.getKey()))
                .findAny()
                .map(Map.Entry::getValue)
                .map(beanType::cast)
                .orElseThrow(() -> new NoSuchBeanException("No such bean."));
    }

    public <T> Map<String, T> getAllBeans(Class<T> beanType) throws NoSuchBeanException, NoUniqueBeanException {
        return beansMap.entrySet()
                .stream()
                .filter(e -> beanType.isAssignableFrom(e.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> beanType.cast(e.getValue())));
    }
}
