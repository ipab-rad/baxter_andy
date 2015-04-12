package com.rad.math;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static junitparams.JUnitParamsRunner.$;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@RunWith(JUnitParamsRunner.class)
public class AngleOffsetApplierTest {

    private AngleOffsetter testAngleOffsetter;
    private double delta = 0.000001;

    /**
     * Test cases to check correct application of offset to original angle
     *
     * Original angle
     * Offset angle
     * Expected angle after offset is applied to original
     * @return
     */
    private static final Object[] getTransformationTestCases() {
        return $(
                $(0, 0, 0),
                $(0, Math.PI/2, -Math.PI/2),
                $(0, -Math.PI/2, Math.PI/2),
                $(0, Math.PI, -Math.PI),
                $(0, -Math.PI, -Math.PI),
                $(-Math.PI/2, -Math.PI, Math.PI/2),
                $(Math.PI/2, -Math.PI, -Math.PI/2),
                $(-Math.PI/2, Math.PI, Math.PI/2),
                $(-3*Math.PI/4, Math.PI, Math.PI/4),
                $(-3*Math.PI/4, -Math.PI/2, -Math.PI/4),
                $(Math.PI, Math.PI/180, 179*Math.PI/180),
                $(Math.PI, -Math.PI/180, -179*Math.PI/180),
                $(5*Math.PI/180, -179*Math.PI/180, -176*Math.PI/180),
                $(-170*Math.PI/180, 170*Math.PI/180, 20*Math.PI/180)
        );
    }

    @Test
    @Parameters(method = "getTransformationTestCases")
    public void givenOriginalAngleAndOffsetApplyingOffsetResultsInCorrectAngle(double original, double offset, double expectedResult) {
        double offsetAngle = AngleOffsetter.applyOffset(original, offset);
        assertThat(offsetAngle).isCloseTo(expectedResult, within(delta));
    }

    @Test
    @Parameters(method = "getTransformationTestCases")
    public void givenOriginalAngleAndOffsetApplyingOffsetSimpleVersionResultsInCorrectAngle(double original, double offset, double expectedResult) {
        double offsetAngle = AngleOffsetter.applyOffsetSimpleVersion(original, offset);
        assertThat(offsetAngle).isCloseTo(expectedResult, within(delta));
    }

}
