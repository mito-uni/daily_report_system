package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import constants.JpaConst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 日報データのDTOモデル
 *
 */
@Table(name = JpaConst.TABLE_REP)
@NamedQueries({
    @NamedQuery(
            name = JpaConst.Q_EMP_GET_ROLE_DIRECTOR,
            query = JpaConst.Q_EMP_GET_ROLE_DIRECTOR_DEF),
    @NamedQuery(
            name = JpaConst.Q_EMP_GET_ROLE_MANAGER,
            query = JpaConst.Q_EMP_GET_ROLE_MANAGER_DEF),
    @NamedQuery(
            name = JpaConst.Q_REP_GET_ALL,
            query = JpaConst.Q_REP_GET_ALL_DEF),
    @NamedQuery(
            name = JpaConst.Q_REP_COUNT,
            query = JpaConst.Q_REP_COUNT_DEF),
    @NamedQuery(
            name = JpaConst.Q_REP_GET_ALL_MINE,
            query = JpaConst.Q_REP_GET_ALL_MINE_DEF),
    @NamedQuery(
            name = JpaConst.Q_REP_COUNT_ALL_MINE,
            query = JpaConst.Q_REP_COUNT_ALL_MINE_DEF),
    @NamedQuery(
            name = JpaConst.Q_REP_GET_APPROVAL_WAIT,
            query = JpaConst.Q_REP_GET_APPROVAL_WAIT_DEF),
    @NamedQuery(
            name = JpaConst.Q_LIK_COUNT_REP,
            query = JpaConst.Q_LIK_COUNT_REP_DEF),
    @NamedQuery(
            name = JpaConst.Q_LIK_COUNT_REP_MINE,
            query = JpaConst.Q_LIK_COUNT_REP_MINE_DEF),
    @NamedQuery(
            name = JpaConst.Q_FOL_COUNT_FOL_MINE,
            query = JpaConst.Q_FOL_COUNT_FOL_MINE_DEF),
})

@Getter //全てのクラスフィールドについてgetterを自動生成する(Lombok)
@Setter //全てのクラスフィールドについてsetterを自動生成する(Lombok)
@NoArgsConstructor //引数なしコンストラクタを自動生成する(Lombok)
@AllArgsConstructor //全てのクラスフィールドを引数にもつ引数ありコンストラクタを自動生成する(Lombok)
@Entity
public class Report {

    /**
     * id
     */
    @Id
    @Column(name = JpaConst.REP_COL_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 日報を登録した従業員
     */
    @ManyToOne
    @JoinColumn(name = JpaConst.REP_COL_EMP, nullable = false)
    private Employee employee;

    /**
     * いつの日報かを示す日付
     */
    @Column(name = JpaConst.REP_COL_REP_DATE, nullable = false)
    private LocalDate reportDate;

    /**
     * 日報のタイトル
     */
    @Column(name = JpaConst.REP_COL_TITLE, length = 255, nullable = false)
    private String title;

    /**
     * 日報の内容
     */
    @Lob
    @Column(name = JpaConst.REP_COL_CONTENT, nullable = false)
    private String content;

    //始業時間
    @Column(name = JpaConst.REP_COL_STARTED_AT, nullable = false)
    private LocalDateTime startedAt;

    //終業時間
    @Column(name = JpaConst.REP_COL_CLOSED_AT, nullable = false)
    private LocalDateTime closedAt;

    /**
     * 登録日時
     */
    @Column(name = JpaConst.REP_COL_CREATED_AT, nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @Column(name = JpaConst.REP_COL_UPDATED_AT, nullable = false)
    private LocalDateTime updatedAt;

    //承認フラグ
    @Column(name = JpaConst.REP_COL_APPROVAL_FLAG, nullable = false)
    private Integer approvalFlag;

    //コメント
    @Lob
    @Column(name = JpaConst.REP_COL_COMMENT)
    private String comment;

}