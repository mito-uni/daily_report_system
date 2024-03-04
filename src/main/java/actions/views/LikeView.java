package actions.views;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//いいね情報について画面の入力値・出力値を扱うViewモデル

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeView {
    //id
    private Integer id;

    //いいねした従業員のid
    private EmployeeView employee;

    //いいねされた日報のid
    private ReportView report;

    //登録日時
    private LocalDateTime createdAt;

    //更新日時
    private LocalDateTime updatedAt;
}
