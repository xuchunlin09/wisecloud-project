package com.lianwuzizai.signature.utils;

@SuppressWarnings("all")
public class Base64Util {
    private static char char62 = '+';
    private static char char63 = '/';

    private static char[] infp = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            char62, char63
    };

    public static final boolean NEED_ADD_ENTER_CODE = true;
    public static final int ENTERCOUNT = 76;
    public static final String ENTER = "";
    public static final int MASK_FLOW_8 = 0x00000000000000ff;
    public static final int MASK_FLOW_6 = 0x000000000000003f;


    private static byte transS2B(char c) {
        byte result = 0;
        if (c >= 'A' && c <= 'Z') {
            result = (byte) (c - 'A');
            return result;
        }
        if (c >= 'a' && c <= 'z') {
            result = (byte) (c - 'a' + 26);
            return result;
        }
        if (c >= '0' && c <= '9') {
            result = (byte) (c - '0' + 52);
            return result;
        }
        if (c == char62) {
            return 62;
        }
        if (c == char63) {
            return 63;
        }
        return result;
    }

    public static String encodeMessage(byte[] message) {
        StringBuilder sb = new StringBuilder();
        int message_byte_length = message.length;
        int end = 3 - message_byte_length % 3;
        if (end == 3) {
            end = 0;
        }
        byte[] operate_message_byte = null;
        if (end > 0) {
            operate_message_byte = new byte[message_byte_length + end];
            System.arraycopy(message, 0, operate_message_byte, 0, message_byte_length);
        } else {
            operate_message_byte = message;
        }
        int line_count = 1;
        for (int i = 0, j = operate_message_byte.length / 3; i < j; i++) {
            int i_byte_real = i * 3;
            sb.append(infp[operate_message_byte[i_byte_real] >> 2 & MASK_FLOW_6]);
            sb.append(infp[((operate_message_byte[i_byte_real] << 4 & 0x30) | (operate_message_byte[i_byte_real + 1] >>> 4 & 0xf)) & MASK_FLOW_6]);
            if (i_byte_real + 1 > message_byte_length - 1) {
                break;
            }
            sb.append(infp[((operate_message_byte[i_byte_real + 1] << 2 & 0x3C) | (operate_message_byte[i_byte_real + 2] >>> 6 & 0x3)) & MASK_FLOW_6]);
            if (i_byte_real + 2 > message_byte_length - 1) {
                break;
            }
            sb.append(infp[operate_message_byte[i_byte_real + 2] & MASK_FLOW_6]);
            if (NEED_ADD_ENTER_CODE && ((line_count + 3) % ENTERCOUNT == 0)) {
                sb.append(ENTER);
            }
            line_count += 4;
        }
        if (end == 2) {
            sb.append("==");
        } else if (end == 1) {
            sb.append('=');
        } else {
        }

        return sb.toString();
    }

    private static char[] clearStringMessage(String message) {
        char[] result = null;
        char[] message_chararray = message.toCharArray();
        int message_chararray_length = message_chararray.length;
        int controlCharCount = 0;
        for (int i = 0; i < message_chararray_length; i++) {
            if (message_chararray[i] == '\r' || message_chararray[i] == '\n') {
                controlCharCount++;
            }
        }
        result = new char[message_chararray_length - controlCharCount];
        for (int i = 0, j = 0; i < message_chararray_length; i++) {
            if (message_chararray[i] != '\r' && message_chararray[i] != '\n') {
                result[j] = message_chararray[i];
                j++;
            }
        }
        return result;
    }

    public static byte[] decodeMessage(String message) {
        char[] message_char = clearStringMessage(message);
        int length = message_char.length / 4 * 3;
        int realLength = 0;
        if (message_char[message_char.length - 2] == '=') {
            realLength = length - 2;
        } else if (message_char[message_char.length - 1] == '=') {
            realLength = length - 1;
        } else {
            realLength = length;
        }

        byte[] result = new byte[realLength];
        for (int i = 0; i < length / 3; i++) {
            int i_base64_real = i * 4;
            int i_byte___real = i * 3;
            byte b1 = transS2B(message_char[i_base64_real]);
            byte b2 = transS2B(message_char[i_base64_real + 1]);
            byte b3 = transS2B(message_char[i_base64_real + 2]);
            byte b4 = transS2B(message_char[i_base64_real + 3]);
            byte bb1 = (byte) ((b1 << 2 & 0xfc) | (b2 >> 4 & 0x03) & MASK_FLOW_8);//1 123456  |  2       12
            byte bb2 = (byte) ((b2 << 4 & 0xf0) | (b3 >> 2 & 0x0f) & MASK_FLOW_8);//2 3456    |  3     1234
            byte bb3 = (byte) ((b3 << 6 & 0xc0) | (b4 & 0x3f) & MASK_FLOW_8);//3 56      |  4   123456
            result[i_byte___real] = bb1;
            if ((i_byte___real + 1) >= realLength) {
                break;
            }
            result[i_byte___real + 1] = bb2;
            if ((i_byte___real + 2) >= realLength) {
                break;
            }
            result[i_byte___real + 2] = bb3;
        }
        return result;
    }

}