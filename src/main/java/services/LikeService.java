package services;

import java.time.LocalDateTime;

import actions.views.LikeConverter;
import actions.views.LikeView;

//いいねテーブルの操作に関わる処理を行うクラス

public class LikeService extends ServiceBase {
    private void create(LikeView lv) {
        LocalDateTime ldt = LocalDateTime.now();
        lv.setCreatedAt(ldt);
        lv.setUpdatedAt(ldt);
        em.getTransaction().begin();
        em.persist(LikeConverter.toModel(lv));
        em.getTransaction().commit();
    }
}
