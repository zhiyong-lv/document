package com.lzy.learning.leecode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class MultiplyNo43 {
    public String multiply(String num1, String num2) {
        String rst = "";
        for (int i = 0; i < num1.length(); i++) {
            int b1 = num1.charAt(num1.length() - 1 - i) - '0';
            for (int j = 0; j < num2.length(); j++) {
                int b2 = num2.charAt(num2.length() - 1 - j) - '0';
                final int bitMultiplyRst = b2 * b1;
                StringBuilder sb = new StringBuilder();
                if (bitMultiplyRst > 0) {
                    sb.append(bitMultiplyRst);
                    for (int k = 0; k < i + j; k++) {
                        sb.append("0");
                    }
                } else {
                    sb.append("0");
                }
                rst = add(rst, sb.toString());
            }
        }
        return rst;
    }

    public String add(String num1, String num2) {
        final int num1Length = num1.length();
        final int num2Length = num2.length();
        final int maxLength = Math.max(num1Length, num2Length);
        StringBuilder sb = new StringBuilder();
        int lastBitAddRst = 0;
        for (int i = 0; i < maxLength; i++) {
            int bitAddRst = lastBitAddRst;

            if (num1Length > i) {
                bitAddRst = num1.charAt(num1Length - 1 - i) - '0' + bitAddRst;
            }

            if (num2Length > i) {
                bitAddRst = num2.charAt(num2Length - 1 - i) - '0' + bitAddRst;
            }

            sb.append(bitAddRst % 10);
            lastBitAddRst = bitAddRst / 10;
        }
        if (lastBitAddRst > 0) {
            sb.append(lastBitAddRst);
        }
        return sb.reverse().toString();
    }

    String merge(List<BitMultiplyResult> bitRsts) {
        StringBuilder sb = new StringBuilder();
        int bit = 0;
        int numFromLowBit = 0;
        for (; !bitRsts.isEmpty(); bit++) {
            final Iterator<BitMultiplyResult> iterator = bitRsts.iterator();
            while (iterator.hasNext()) {
                final BitMultiplyResult multiplyResult = iterator.next();
                final Optional<Integer> bitOptional = multiplyResult.bitAt(bit);
                if (!bitOptional.isPresent()) {
                    iterator.remove();
                    continue;
                }
                numFromLowBit += bitOptional.get();
            }
            if (numFromLowBit > 0) {
                sb.append(numFromLowBit % 10);
                numFromLowBit /= 10;
            } else {
                break;
            }
        }

        return sb.reverse().toString();
    }

    BitMultiplyResult multiply(String num1, int num2Bit, int bitFrom) {
        if (num2Bit == 0) {
            return new BitMultiplyResult("0", bitFrom);
        }

        if (num2Bit == 1) {
            return new BitMultiplyResult(num1, bitFrom);
        }

        final int length = num1.length();
        int numFromLowBit = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int bitMultiplyRst = (num1.charAt(length - i - 1) - '0') * num2Bit + numFromLowBit;
            sb.append(bitMultiplyRst % 10);
            numFromLowBit = bitMultiplyRst / 10;
        }
        if (numFromLowBit > 0) {
            sb.append(numFromLowBit);
        }
        return new BitMultiplyResult(sb.reverse().toString(), bitFrom);
    }

    @ParameterizedTest
    @CsvSource({
            "2,3,6",
            "123,456,56088",
            "9133,0,0",
    })
    void multiplyTest(String num1, String num2, String expectedRst) {
        Assertions.assertEquals(expectedRst, multiply(num1, num2));
        Assertions.assertEquals(expectedRst, multiply(num2, num1));
    }

    @ParameterizedTest
    @CsvSource({
            "123,456,579",
            "123,457,580",
            "123,957,1080",
            "999,999,1998",
            "999,99,1098",
            "999,'',999",
    })
    void addTest(String num1, String num2, String expectedRst) {
        Assertions.assertEquals(expectedRst, add(num1, num2));
        Assertions.assertEquals(expectedRst, add(num2, num1));
    }

    class BitMultiplyResult {
        final String rst;
        final int bitFrom;
        final int length;

        BitMultiplyResult(String rst, int bitFrom) {
            this.rst = rst;
            this.bitFrom = bitFrom;
            this.length = rst.length() + this.bitFrom;
        }

        int length() {
            return length;
        }

        Optional<Integer> bitAt(int i) {
            if (i >= length) {
                return Optional.empty();
            }

            if (i < bitFrom) {
                return Optional.of(0);
            }

            return Optional.of(rst.charAt(rst.length() - (i - bitFrom) - 1) - '0');
        }
    }
}
