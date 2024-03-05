package services;

import java.time.LocalDateTime;

import actions.views.LikeConverter;
import actions.views.LikeView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import models.Report;

//いいねテーブルの操作に関わる処理を行うクラス

public class LikeService extends ServiceBase {

    public ReportView findOne(int id) {
        return ReportConverter.toView(findOneInternal(id));
    }

    public void create(LikeView lv) {
        LocalDateTime ldt = LocalDateTime.now();
        lv.setCreatedAt(ldt);
        lv.setUpdatedAt(ldt);
        createInternal(lv);
    }

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
}
