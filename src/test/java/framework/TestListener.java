package framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("=== Starting Test Suite: {} ===", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("=== Finished Test Suite: {} ===", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("[TEST START] {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("[PASS] {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("[FAIL] {} - Reason: {}", result.getMethod().getMethodName(), result.getThrowable().getMessage(), result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("[SKIP] {}", result.getMethod().getMethodName());
    }
}
