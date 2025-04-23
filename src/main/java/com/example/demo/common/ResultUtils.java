package com.example.demo.common;

/**
 * 返回工具类
 */
public class ResultUtils {

    /**
     *  成功
     * @param data
     * @return
     * @param <T>
     */
    public  static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0,data,"ok");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public  static BaseResponse error(ErrorCode errorCode) {

        return new BaseResponse<>(errorCode);
    }

    /**
     *
     * @param code
     * @param message
     * @param description
     * @return 这儿
     */
    public static BaseResponse error(int code,String message,String description) {
        System.out.println(123);
        return new BaseResponse(code,null,message,description);
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public  static BaseResponse error(ErrorCode errorCode,String message,String description) {
        return new BaseResponse(errorCode.getCode(),null,message,description);
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public  static BaseResponse error(ErrorCode errorCode,String description) {
        return new BaseResponse(errorCode.getCode(),errorCode.getMessage(),description);
    }


}
