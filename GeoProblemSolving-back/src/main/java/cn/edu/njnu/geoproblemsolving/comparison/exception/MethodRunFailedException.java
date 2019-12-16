package cn.edu.njnu.geoproblemsolving.comparison.exception;

import cn.edu.njnu.geoproblemsolving.comparison.enums.ResultEnum;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 16:33 2019/11/14
 * @Modified By:
 **/
public class MethodRunFailedException extends RuntimeException {
    private Integer code;

    public MethodRunFailedException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }

    public MethodRunFailedException(String message) {
        super(message);
        this.code = -9998;
    }

    public Integer getCode() {
        return code;
    }
}
