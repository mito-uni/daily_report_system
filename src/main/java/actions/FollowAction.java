package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.FollowView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import models.Follow;
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

    public void index() throws ServletException, IOException {
        //セッションからログイン中の従業員情報を取得
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        //ログイン中の従業員がフォローした従業員データを、指定されたページ数の一覧画面に表示する分取得する
        int page = getPage();
        List<FollowView> follows = service.getMinePerPage(ev, page);

        //ログイン中の従業員がフォローした従業員データの件数を取得
        long FollowsCount = service.countAllMine(ev);

        putRequestScope(AttributeConst.FOLLOWS, follows); //取得したフォロー済み従業員のデータ
        putRequestScope(AttributeConst.FOL_COUNT_MINE, FollowsCount); //ログイン中の従業員がフォローした従業員の数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

      //一覧画面を表示
        forward(ForwardConst.FW_FOL_INDEX);
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

        //セッションに登録完了のフラッシュメッセージを設定
        putSessionScope(AttributeConst.FLUSH, MessageConst.I_FOLLOWED.getMessage());

        redirect(ForwardConst.ACT_FOL, ForwardConst.CMD_INDEX);
    }

    public void show() throws ServletException, IOException {
        //idを条件に従業員情報を取得
        EmployeeView employee = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        String name = employee.getName();

        //指定の従業員が作成した日報データを、指定されたページ数の一覧画面に表示する分取得する
        int page = getPage();
        List<ReportView> reports = service.getMineEmployeePerPage(employee, page);

        //指定の従業員が作成した日報データの件数を取得
        long myFollowReportsCount = service.countAllMineEmployee(employee);

        putRequestScope(AttributeConst.REPORTS, reports); //取得した日報データ
        putRequestScope(AttributeConst.REP_COUNT, myFollowReportsCount); //指定の従業員が作成した日報の数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数
        putRequestScope(AttributeConst.EMP_NAME, name);

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_FOL_SHOW);
    }

    public void destroy() throws ServletException, IOException {
        //セッションからログイン中の従業員情報を取得
        EmployeeView following = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        //employee_idを条件に日報を作成した従業員データを取得する
        EmployeeView followed = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        //上記の2つの従業員情報から、フォローデータを取得する
        Follow f = service.getFollowEmployee(following, followed);

        //フォローデータの削除
        service.destroy(f);

        //セッションに削除完了のフラッシュメッセージを設定
        putSessionScope(AttributeConst.FLUSH, MessageConst.I_FOLLOW_DELETED.getMessage());

        redirect(ForwardConst.ACT_FOL, ForwardConst.CMD_INDEX);
    }
}