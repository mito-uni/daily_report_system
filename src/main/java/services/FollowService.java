package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.FollowConverter;
import actions.views.FollowView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Employee;
import models.Follow;
import models.Report;

//フォローテーブルの操作に関わる処理を行うクラス

public class FollowService extends ServiceBase {
    /**
     * 指定した従業員が作成した日報データを、指定されたページ数の一覧画面に表示する分取得しReportViewのリストで返却する
     * @param employee 従業員
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト
     */
    public List<ReportView> getMineEmployeePerPage(EmployeeView employee, int page) {

        List<Report> reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_MINE, Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }

    /**
     * 指定した従業員が作成した日報データの件数を取得し、返却する
     * @param employee
     * @return 日報データの件数
     */
    public long countAllMineEmployee(EmployeeView employee) {

        long count = (long) em.createNamedQuery(JpaConst.Q_REP_COUNT_ALL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .getSingleResult();

        return count;
    }

    /*
     * 指定した従業員が作成したフォローデータを、指定されたページ数の一覧画面に表示する分取得しFollowViewのリストで返却する
     * @return 一覧画面に表示するデータのリスト
     */
    public List<FollowView> getMinePerPage(EmployeeView employee, int page) {
        List<Follow> follows = em.createNamedQuery(JpaConst.Q_FOL_GET_ALL_MINE, Follow.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOWING, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page -1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();

        return FollowConverter.toViewList(follows);
    }

    /*
     * 指定した従業員が作成したフォローデータの件数を取得し、返却する
     */

    public long countAllMine(EmployeeView employee) {
        long count = (long) em.createNamedQuery(JpaConst.Q_FOL_COUNT_GET_ALL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOWING, EmployeeConverter.toModel(employee))
                .getSingleResult();

        return count;
    }

    //指定のフォローレコードを取得する
    public Follow getFollowEmployee(EmployeeView following, EmployeeView followed) {
        Follow follow = em.createNamedQuery(JpaConst.Q_FOL_GET_FOL, Follow.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOWING, EmployeeConverter.toModel(following))
                .setParameter(JpaConst.JPQL_PARM_FOLLOWED, EmployeeConverter.toModel(followed))
                .getSingleResult();
        return follow;
    }

    /*
     * idを条件に取得したデータをEmployeeViewのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    public EmployeeView findOne(int id) {
        return EmployeeConverter.toView(findOneInternal(id));
    }

    /*
     * 画面の情報からフォローデータを1件作成し、フォローテーブルに登録する
     */
    public void create(FollowView fv) {
        LocalDateTime ldt = LocalDateTime.now();
        fv.setCreatedAt(ldt);
        fv.setUpdatedAt(ldt);
        createInternal(fv);
    }

    //指定のフォローデータの削除
    public void destroy(Follow f) {
        destroyInternal(f);
    }

    /*
     * idを条件にデータを1件取得する
     * @param id
     * @return 取得データのインスタンス
     */
    private Employee findOneInternal(int id) {
        return em.find(Employee.class, id);
    }

    /*
     * フォローデータを1件登録する
     */
    private void createInternal(FollowView fv) {
        em.getTransaction().begin();
        em.persist(FollowConverter.toModel(fv));
        em.getTransaction().commit();
    }

    //フォローデータの削除
    private void destroyInternal(Follow f) {
        em.getTransaction().begin();
        em.remove(f);
        em.getTransaction().commit();
        em.close();
    }
}
