package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import next.reflection.ReflectionTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BeanFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactoryTest.class);

    private Reflections reflections;
    private BeanFactory beanFactory;


    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        reflections = new Reflections("core.di.factory.example");
        Set<Class<?>> preInstanticateClazz = getTypesAnnotatedWith(Controller.class, Service.class, Repository.class);
        beanFactory = new BeanFactory(preInstanticateClazz);
        beanFactory.initialize();
    }

    @Test
    public void di() throws Exception {
        QnaController qnaController = beanFactory.getBean(QnaController.class);

        assertNotNull(qnaController);
        assertNotNull(qnaController.getQnaService());

        MyQnaService qnaService = qnaController.getQnaService();
        assertNotNull(qnaService.getUserRepository());
        assertNotNull(qnaService.getQuestionRepository());
    }

    @SuppressWarnings("unchecked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    @DisplayName("component scan")
    @Test
    public void componentScanTest() {
        Reflections reflections = new Reflections("core.di.factory.example");
        Set<Class<?>> annotatedControllerClasses = getTypesAnnotatedWith(reflections, Controller.class);
        Set<Class<?>> annotatedServiceClasses = getTypesAnnotatedWith(reflections, Service.class);
        Set<Class<?>> annotatedRepositoryClasses = getTypesAnnotatedWith(reflections, Repository.class);

        for (Class<?> annotatedControllerClass : annotatedControllerClasses) {
            logger.debug("annotatedControllerClass : {}", annotatedControllerClass);
        }

        for (Class<?> annotatedServiceClass : annotatedServiceClasses) {
            logger.debug("annotatedServiceClass : {}", annotatedServiceClass);
        }

        for (Class<?> annotatedRepositoryClass : annotatedRepositoryClasses) {
            logger.debug("annotatedRepositoryClass : {}", annotatedRepositoryClass);
        }
    }

    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation> annotation) {
        return reflections.getTypesAnnotatedWith(annotation);
    }
}
