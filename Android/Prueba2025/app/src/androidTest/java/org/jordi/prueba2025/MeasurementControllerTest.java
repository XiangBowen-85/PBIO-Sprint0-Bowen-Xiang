package org.jordi.prueba2025;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MeasurementControllerTest {

    @Test
    public void becomesReadyAfterBothMeasurements() {
        MeasurementController controller =
                new MeasurementController("XBW01");

        controller.processManufacturerData(
                createManufacturerData(
                        ParsedMeasurement.TYPE_CO2,
                        20,
                        456
                )
        );

        assertFalse(
                controller.isReadyToUpload()
        );

        MeasurementData data =
                controller.getMeasurementData();

        data.setRawData("02:15:00");
        data.setDeviceAddress(
                "FA:46:B4:94:6C:0C"
        );
        data.setRssi(-60);

        controller.processManufacturerData(
                createManufacturerData(
                        ParsedMeasurement.TYPE_TEMPERATURE,
                        20,
                        -24
                )
        );

        assertTrue(
                controller.isReadyToUpload()
        );

        assertEquals(
                Integer.valueOf(456),
                data.getCo2Value()
        );

        assertEquals(
                Integer.valueOf(-24),
                data.getTemperatureValue()
        );
    }

    @Test
    public void resetRemovesPreviousMeasurements() {
        MeasurementController controller =
                new MeasurementController("XBW01");

        MeasurementData data =
                controller.getMeasurementData();

        data.setRawData("02:15:00");

        controller.processManufacturerData(
                createManufacturerData(
                        ParsedMeasurement.TYPE_CO2,
                        1,
                        456
                )
        );

        controller.processManufacturerData(
                createManufacturerData(
                        ParsedMeasurement.TYPE_TEMPERATURE,
                        1,
                        -24
                )
        );

        assertTrue(
                controller.isReadyToUpload()
        );

        controller.reset();

        assertFalse(
                controller.isReadyToUpload()
        );
    }

    private byte[] createManufacturerData(
            int type,
            int counter,
            int value
    ) {
        byte[] result = new byte[23];

        result[0] = 0x02;
        result[1] = 0x15;

        byte[] uuid =
                "BOWEN-XBW01-2026"
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        System.arraycopy(
                uuid,
                0,
                result,
                2,
                uuid.length
        );

        int major =
                ((type & 0xFF) << 8) |
                        (counter & 0xFF);

        result[18] =
                (byte) (
                        (major >> 8) & 0xFF
                );

        result[19] =
                (byte) (
                        major & 0xFF
                );

        int valueBits =
                value & 0xFFFF;

        result[20] =
                (byte) (
                        (valueBits >> 8) & 0xFF
                );

        result[21] =
                (byte) (
                        valueBits & 0xFF
                );

        result[22] =
                (byte) 0xCB;

        return result;
    }
}