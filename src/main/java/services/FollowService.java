package services;

import java.time.LocalDateTime;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.FollowConverter;
import actions.views.FollowView;
import models.Employee;

//フォローテーブルの操作に関わる処理を行うクラス

public class FollowService extends ServiceBase {
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
}
