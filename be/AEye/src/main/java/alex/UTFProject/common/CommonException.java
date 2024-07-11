package alex.UTFProject.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonException extends RuntimeException {
    private CommonErrorCode commonErrorCode;
    private Object errorMsg;

    public CommonException(CommonErrorCode commonErrorCode) {
        this.commonErrorCode = commonErrorCode;
    }
}