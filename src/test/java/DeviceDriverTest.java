import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeviceDriverTest {
    DeviceDriver driver;

    @Mock
    FlashMemoryDevice hardware;

    @BeforeEach
    void setUp() {
        driver = new DeviceDriver(hardware);
    }

    @Test
    public void read_From_Hardware() {
        byte data = driver.read(0xFF);
        assertEquals(0, data);
    }

    @Test
    void readFiveTime() {
        byte data = driver.read(0xAA);

        verify(hardware, times(5)).read(0xAA);
    }

    @Test
    void writeReadTime() {
        when(hardware.read(0xBB))
                .thenReturn((byte) 0xFF);

        driver.write(0xBB, (byte) 0x01);

        verify(hardware, times(1)).read(0xBB);
        verify(hardware, times(1)).write(0xBB, (byte) 0x01);
    }

    @Test
    void readFailTest() {
        //hardware read method setup
        try {
            when(hardware.read(0xFF))
                    .thenReturn((byte) 0x01)
                    .thenReturn((byte) 0x00)
                    .thenReturn((byte) 0x01)
                    .thenReturn((byte) 0x01)
                    .thenReturn((byte) 0x01);

            byte data = driver.read(0xFF);
            fail();
        } catch (ReadFailException e) {
            assertThat(e.getMessage()).isEqualTo("Read Fail From Flash Memory");
        }
    }

    @Test
    void writeFailTest() {
        //hardware read method setup
        try {
            when(hardware.read(0xFF))
                    .thenReturn((byte) 0x00);

            driver.write(0xFF, (byte) 0x01);
            fail();
        } catch (WriteFailException e) {
            assertThat(e.getMessage()).isEqualTo("Write Fail From Flash Memory");
        }
    }

    @Test
    void readAndPrintTest() {
        long startAddr = 0xA0;
        long endAddr = 0xBF;
        driver.readAndPrint(0xA0, 0xBF);

        for (long addr = startAddr; addr <= endAddr; addr++) {
            verify(hardware, times(5)).read(addr);
        }
        //verify(hardware, times(5*(int)(endAddr-startAddr+1))).read(anyLong());
    }

    @Test
    void writeAllTest() {
        when(hardware.read(0x00)).thenReturn((byte) 0xFF);
        when(hardware.read(0x01)).thenReturn((byte) 0xFF);
        when(hardware.read(0x02)).thenReturn((byte) 0xFF);
        when(hardware.read(0x03)).thenReturn((byte) 0xFF);
        when(hardware.read(0x04)).thenReturn((byte) 0xFF);

        driver.writeAll((byte) 0x01);

        //verify(hardware, times(5)).read(anyLong());
        //verify(hardware, times(5)).write(anyLong(), anyByte());

        verify(hardware, times(1)).read(0x00);
        verify(hardware, times(1)).write(0x00, (byte) 0x01);
        verify(hardware, times(1)).read(0x01);
        verify(hardware, times(1)).write(0x01, (byte) 0x01);
        verify(hardware, times(1)).read(0x02);
        verify(hardware, times(1)).write(0x02, (byte) 0x01);
        verify(hardware, times(1)).read(0x03);
        verify(hardware, times(1)).write(0x03, (byte) 0x01);
        verify(hardware, times(1)).read(0x04);
        verify(hardware, times(1)).write(0x04, (byte) 0x01);
    }
}