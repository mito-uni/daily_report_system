package actions.views;

import java.util.ArrayList;
import java.util.List;

import constants.AttributeConst;
import constants.JpaConst;
import models.Report;

/**
 * 日報データのDTOモデル⇔Viewモデルの変換を行うクラス
 *
 */
public class ReportConverter {

    /**
     * ViewモデルのインスタンスからDTOモデルのインスタンスを作成する
     * @param rv ReportViewのインスタンス
     * @return Reportのインスタンス
     */
    public static Report toModel(ReportView rv) {
        return new Report(
                rv.getId(),
                EmployeeConverter.toModel(rv.getEmployee()),
                rv.getReportDate(),
                rv.getTitle(),
                rv.getContent(),
                rv.getStartedAt(),
                rv.getClosedAt(),
                rv.getCreatedAt(),
                rv.getUpdatedAt(),
                rv.getApprovalFlag() == null
                    ? null
                    : rv.getApprovalFlag() == AttributeConst.APPROVAL_FLAG_FALSE.getIntegerValue()
                        ? JpaConst.APPROVAL_FLAG_FALSE
                        : rv.getApprovalFlag() == AttributeConst.APPROVAL_FLAG_TRUE.getIntegerValue()
                            ? JpaConst.APPROVAL_FLAG_TRUE
                            : JpaConst.APPROVAL_FLAG_WAIT,
                rv.getComment());
    }

    /**
     * DTOモデルのインスタンスからViewモデルのインスタンスを作成する
     * @param r Reportのインスタンス
     * @return ReportViewのインスタンス
     */
    public static ReportView toView(Report r) {

        if (r == null) {
            return null;
        }

        return new ReportView(
                r.getId(),
                EmployeeConverter.toView(r.getEmployee()),
                r.getReportDate(),
                r.getTitle(),
                r.getContent(),
                r.getStartedAt(),
                r.getClosedAt(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                r.getApprovalFlag() == null
                    ? null
                    : r.getApprovalFlag() == JpaConst.APPROVAL_FLAG_FALSE
                        ? AttributeConst.APPROVAL_FLAG_FALSE.getIntegerValue()
                        : r.getApprovalFlag() == JpaConst.APPROVAL_FLAG_TRUE
                            ? AttributeConst.APPROVAL_FLAG_TRUE.getIntegerValue()
                            : AttributeConst.APPROVAL_FLAG_WAIT.getIntegerValue(),
                r.getComment());
    }

    /**
     * DTOモデルのリストからViewモデルのリストを作成する
     * @param list DTOモデルのリスト
     * @return Viewモデルのリスト
     */
    public static List<ReportView> toViewList(List<Report> list) {
        List<ReportView> evs = new ArrayList<>();

        for (Report r : list) {
            evs.add(toView(r));
        }

        return evs;
    }

    /**
     * Viewモデルの全フィールドの内容をDTOモデルのフィールドにコピーする
     * @param r DTOモデル(コピー先)
     * @param rv Viewモデル(コピー元)
     */
    public static void copyViewToModel(Report r, ReportView rv) {
        r.setId(rv.getId());
        r.setEmployee(EmployeeConverter.toModel(rv.getEmployee()));
        r.setReportDate(rv.getReportDate());
        r.setTitle(rv.getTitle());
        r.setContent(rv.getContent());
        r.setStartedAt(rv.getStartedAt());
        r.setClosedAt(rv.getClosedAt());
        r.setCreatedAt(rv.getCreatedAt());
        r.setUpdatedAt(rv.getUpdatedAt());
        r.setApprovalFlag(rv.getApprovalFlag());
        r.setComment(rv.getComment());

    }

}