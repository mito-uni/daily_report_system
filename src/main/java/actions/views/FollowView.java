package actions.views;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//フォロー情報について画面の入力値・出力値を扱うViewモデル

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowView {
    //id
    private Integer id;

    //フォローした従業員
    private EmployeeView following;

    //フォローされた従業員
    private EmployeeView followed;

    //作成日時
    private LocalDateTime createdAt;

    //更新日時
    private LocalDateTime updatedAt;
}
