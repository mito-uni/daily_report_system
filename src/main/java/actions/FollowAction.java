package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.FollowView;
import constants.AttributeConst;
import constants.ForwardConst;
import services.FollowService;

public class FollowAction extends ActionBase {
    private FollowService service;

    @Override
    public void process() throws ServletException, IOException {
        service = new FollowService();

        //メソッドを実行
        invoke();
        service.close();
    }

    public void create() throws ServletException, IOException {
        //セッションからログイン中の従業員情報を取得
        EmployeeView following = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        //employee_idを条件に日報を作成した従業員データを取得する
        EmployeeView followed = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        //パラメータの値をもとにフォロー情報のインスタンスを作成する
        FollowView fv = new FollowView(
                null,
                following,
                followed,
                null,
                null);

        //フォロー情報登録
        service.create(fv);
        redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
    }
}
