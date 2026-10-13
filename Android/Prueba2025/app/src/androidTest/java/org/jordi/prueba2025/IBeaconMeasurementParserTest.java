package org.jordi.prueba2025;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IBeaconMeasurementParserTest {

    private final IBeaconMeasurementParser parser =
            new IBeaconMeasurementParser();

    @Test
    public void parsesCo2Value456() {
        byte[] data = createManufacturerData(
                ParsedMeasurement.TYPE_CO2,
                12,
                456
        );

        ParsedMeasurement result =
                parser.parse(data);

        assertTrue(result.isCo2());
        assertEquals(12, result.getCounter());
        assertEquals(456, result.getValue());
    }

    @Test
    public void parsesNegativeTemperatureMinus24() {
        byte[] data = createManufacturerData(
                ParsedMeasurement.TYPE_TEMPERATURE,
                12,
                -24
        );

        ParsedMeasurement result =
                parser.parse(data);

        assertTrue(result.isTemperature());
        assertEquals(12, result.getCounter());
        assertEquals(-24, result.getValue());
    }

    @Test(
            expected = IllegalArgumentException.class
    )
    public void rejectsUnexpectedUuid() {
        byte[] data = createManufacturerData(
                ParsedMeasurement.TYPE_CO2,
                1,
                456
        );

        data[2] = (byte) 'X';

        parser.parse(data);
    }

    @Test(
            expected = IllegalArgumentException.class
    )
    public void rejectsUnknownMeasurementType() {
        byte[] data = createManufacturerData(
                14,
                1,
                1234
        );

        parser.parse(data);
    }

    @Test(
            expected = IllegalArgumentException.class
    )
    public void rejectsShortManufacturerData() {
        parser.parse(
                new byte[] {
                        0x02,
                        0x15
                }
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