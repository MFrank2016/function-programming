package com.mfrank.functionprogram.base;

import org.junit.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

import static com.mfrank.functionprogram.base.Case.match;
import static com.mfrank.functionprogram.base.Case.mcase;
import static com.mfrank.functionprogram.base.Result.failure;
import static com.mfrank.functionprogram.base.Result.success;
import static com.mfrank.functionprogram.base.TailCall.ret;
import static com.mfrank.functionprogram.base.TailCall.sus;
import static com.mfrank.functionprogram.util.CollectionUtility.*;

public class FunctionTest {

    @Test
    public void addFuntion() {
        Function<Integer, Function<Integer, Integer>> add = x -> y -> x + y;
        Integer result = add.apply(2).apply(3);
        System.out.println(result);

        BinaryOperator<Integer> add1 = x -> y -> x + y;
        Integer result1 = add1.apply(2).apply(5);
        System.out.println(result1);
    }

    @Test
    public void triple() {
        Function<Integer, Integer> triple = i -> i * 3;
        Integer result = triple.apply(3);
        assert result == 9;

        Function<Integer, Integer> square = i -> i * i;
        Integer result2 = square.apply(9);
        assert result2 == 81;
    }

    @Test
    public void higherCompose() {
        Function<Double, Double> f = x -> Math.PI / 2 - x;
        Function<Double, Double> sin = Math::sin;
        Double cos = Function.compose(f, sin).apply(2.0);
        System.out.println(cos);

        Double result = Function.compose(x -> Math.PI / 2 - x, Math::sin).apply(3.0);
        System.out.println(result);

        Double result2 = Function.<Double, Double, Double>higherCompose().apply(z -> Math.PI / 2 - z).apply(Math::sin).apply(4.0);
        System.out.println(result2);
    }

    public final Function<Integer, Integer> factorial = n -> n <= 1 ? n : n * this.factorial.apply(n - 1);

    @Test
    public void factorial() {
        System.out.println(factorial.apply(3));
        System.out.println(factorial.apply(5));
    }

    static Pattern emailPattern = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9._]+\\.[a-z]{2,4}$");

    static Effect<String> success = s -> System.out.println("Mail send to " + s);

    static Effect<String> failure = s -> System.out.println("Error message logged:" + s);

    static Function<String, Result<String>> emailChecker = s -> match(
            mcase(() -> success(s)),
            mcase(() -> s == null, () -> failure("email must not be null")),
            mcase(() -> s.length() == 0, () -> failure("email must not be empty")),
            mcase(() -> !emailPattern.matcher(s).matches(), () -> failure("email " + s + " is invalid"))
    );

    @Test
    public void testSendMail() {
        emailChecker.apply("this.is@my.email").bind(success, failure);
        emailChecker.apply(null).bind(success, failure);
        emailChecker.apply("").bind(success, failure);
        emailChecker.apply("join.doe@acme.com").bind(success, failure);
    }


    static int add(int x, int y) {
        while (y-- > 0) {
            x++;
        }
        return x;
    }

    static int addRec(int x, int y) {
        return y == 0
                ? x
                : addRec(++x, --y);
    }

    @Test
    public void addTest() {
        int result = add(10000, 100001);
        assert result == 10000 + 100001;


        int result1 = addRec(100, 1000);
        assert result1 == 100 + 1000;


//        int result2 = addRec(10000, 100001);
//        assert result2 == 10000 + 100001;
        long time = System.currentTimeMillis();
        int result3 = add.apply(100000).apply(20000000).eval();
        assert result3 == 100000 + 20000000;
        System.out.println("计算耗时：" + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        int result4 = add2.apply(100000).apply(20000000);
        assert result4 == 100000 + 20000000;
        System.out.println("计算耗时：" + (System.currentTimeMillis() - time));
    }

    static Function<Integer, Function<Integer, TailCall<Integer>>> add = a -> b -> b == 0
            ? ret(a)
            : sus(() -> FunctionTest.add.apply(a + 1).apply(b - 1));

    static Function<Integer, Function<Integer, Integer>> add2 = x -> y -> {
        class AddHelper {
            Function<Integer, Function<Integer, TailCall<Integer>>> addHelper = a -> b -> b == 0
                    ? ret(a)
                    : sus(() -> this.addHelper.apply(a + 1).apply(b - 1));
        }
        return new AddHelper().addHelper.apply(x).apply(y).eval();
    };


    @Test
    public void fibTest() {
        BigInteger result = fib(100000);
        System.out.println(result);
    }

    private static BigInteger fib(int x) {
        return fib_(BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(x)).eval();
    }

    private static TailCall<BigInteger> fib_(BigInteger acc1, BigInteger acc2, BigInteger x) {
        if (x.equals(BigInteger.ZERO)) {
            return ret(BigInteger.ZERO);
        } else if (x.equals(BigInteger.ONE)) {
            return ret(acc1.add(acc2));
        } else {
            return sus(() -> fib_(acc2, acc1.add(acc2), x.subtract(BigInteger.ONE)));
        }
    }


    public static String addSI(String s, Integer i) {
        return "(" + s + " + " + i + ")";
    }

    public static String addIS(Integer i, String s) {
        return "(" + i + " + " + s + ")";
    }

    @Test
    public void testFold() {
        List<Integer> list = list(1, 2, 3, 4, 5);
        String identity = "0";
        Function<String, Function<Integer, String>> f = x -> y -> addSI(x, y);
        String s = foldLeft(list, identity, f);
        System.out.println(s);

        Function<Integer, Function<String, String>> f2 = x -> y -> addIS(x, y);
        String s2 = foldRight(list, identity, f2);
        System.out.println(s2);
    }
}