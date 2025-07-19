package com.example.demo.service;

import com.example.demo.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 算法测试类
 */
public class AlgorithmUtilsTest {
    /**
     * 编辑距离算法，计算最相似的两个字符串
     * @return
     */

    @Test
    public void testCompareTags() {
        List<String> s1=Arrays.asList("java","大一","男");
        List<String> s2=Arrays.asList("java","大二","男");
        List<String> s3=Arrays.asList("python","大一","女");
        int score1= AlgorithmUtils.minDistance(s1, s2);
        int score2=AlgorithmUtils.minDistance(s1, s3);
        int score3=AlgorithmUtils.minDistance(s2, s3);
        System.out.println(score1);
        System.out.println(score2);
        System.out.println(score3);
    }


}
