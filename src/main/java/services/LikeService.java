package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.LikeConverter;
import actions.views.LikeView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Like;
import models.Report;

//いいねテーブルの操作に関わる処理を行うクラス

public class LikeService extends ServiceBase {
    /*
     * 指定した従業員が作成したいいねデータを、指定されたページ数の一覧画面に表示する分取得しLikeViewのリストで返却する
     * @return 一覧画面に表示するデータのリスト
     */
    public List<LikeView> getMinePerPage(EmployeeView employee, int page) {
        List<Like> likes = em.createNamedQuery(JpaConst.Q_LIK_GET_ALL_MINE, Like.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page -1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();

        return LikeConverter.toViewList(likes);
    }

    /*
     * 指定した従業員が作成したいいねデータの件数を取得し、返却する
     */

    public long countAllMine(EmployeeView employee) {
        long count = (long) em.createNamedQuery(JpaConst.Q_LIK_COUNT_GET_ALL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .getSingleResult();

        return count;
    }

    /*
     * 指定した従業員の指定した日報に対するいいねを取得
     */

    public Like getLikeReport(EmployeeView employee, ReportView report) {
        Like like = em.createNamedQuery(JpaConst.Q_LIK_GET_REP_LIK, Like.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setParameter(JpaConst.JPQL_PARM_REPORT, ReportConverter.toModel(report))
                .getSingleResult();

        return like;
    }

    /**
     * idを条件に取得したデータをReportViewのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    public ReportView findOne(int id) {
        return ReportConverter.toView(findOneInternal(id));
    }

    /**
     * 画面の日報情報を元にいいねのデータを1件作成し、いいねテーブルに登録する
     * @param lv いいねの登録内容
     */
    public void create(LikeView lv) {
        LocalDateTime ldt = LocalDateTime.now();
        lv.setCreatedAt(ldt);
        lv.setUpdatedAt(ldt);
        createInternal(lv);
    }

    public void destroy(Like l) {
        destroyInternal(l);
    }

    /**
     * idを条件にデータを1件取得する
     * @param id
     * @return 取得データのインスタンス
     */
    private Report findOneInternal(int id) {
        return em.find(Report.class, id);
    }

    /**
     * いいねデータを1件登録する
     * @param lv いいねデータ
     */
    private void createInternal(LikeView lv) {

        em.getTransaction().begin();
        em.persist(LikeConverter.toModel(lv));
        em.getTransaction().commit();
    }

    /*
     * いいねデータを削除する
     */
    private void destroyInternal(Like l) {
        em.getTransaction().begin();
        em.remove(l);
        em.getTransaction().commit();
        em.close();
    }
}
