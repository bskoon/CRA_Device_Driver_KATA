/**
 * This class is used by the operating system to interact with the hardware 'FlashMemoryDevice'.
 */
public class DeviceDriver {

    FlashMemoryDevice hw;

    public DeviceDriver(FlashMemoryDevice hardware) {
        hw = hardware;
    }

    public byte read(long address) {
        byte data = hw.read(address);
        checkReadPostCondition(address, data);
        return data;
    }

    private void checkReadPostCondition(long address, byte data) {
        for(int testTurn = 0; testTurn < 4; testTurn++) {
            byte currentData = hw.read(address);
            if (data != currentData) {
                throw new ReadFailException("Read Fail From Flash Memory");
            }
        }
    }

    public void write(long address, byte data) {
        checkWritePreCondition(address);
        hw.write(address, data);
    }

    private void checkWritePreCondition(long address) {
        byte readData = hw.read(address);
        if (readData != (byte) 0xFF) {
            throw new WriteFailException("Write Fail From Flash Memory");
        }
    }

    public void readAndPrint(long startAddr, long endAddr) {
        if (endAddr < startAddr) {
            throw new ReadFailException("ReadAndPrint Fail");
        }

        for (long addr = startAddr; addr <= endAddr; addr++) {
            System.out.println(read(addr));
        }
    }

    public void writeAll(byte value) {
        write(0x00, value);
        write(0x01, value);
        write(0x02, value);
        write(0x03, value);
        write(0x04, value);
    }
}