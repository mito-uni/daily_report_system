package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.LikeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import services.LikeService;

public class LikeAction extends ActionBase {
    private LikeService service;

    @Override
    public void process() throws ServletException, IOException {
        service = new LikeService();

        //メソッドを実行
        invoke();
        service.close();
    }

    public void create() throws ServletException, IOException {

        //セッションからログイン中の従業員情報を取得
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);
      //idを条件に日報データを取得する
        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        LikeView lv = new LikeView(
                null,
                ev,
                rv,
                null,
                null);

        service.create(lv);
        redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);

    }
}
