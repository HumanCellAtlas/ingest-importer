package uk.ac.ebi.hca.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IngestTestRunner extends BlockJUnit4ClassRunner {

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the test class is malformed.
     */
    public IngestTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> defaultTests = super.computeTestMethods();
        List<FrameworkMethod> customTests = getTestClass()
                .getAnnotatedMethods(IntegrationTest.class);
        List<FrameworkMethod> allTests = new ArrayList<>(defaultTests);
        allTests.addAll(customTests);
        return Collections.unmodifiableList(allTests);
    }

}
