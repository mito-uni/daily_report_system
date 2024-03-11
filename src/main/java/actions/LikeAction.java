package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.LikeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import models.Like;
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
    public void index() throws ServletException, IOException {
        //セッションからログイン中の従業員情報を取得
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        //ログイン中の従業員が作成したいいねデータを、指定されたページ数の一覧画面に表示する分取得する
        int page = getPage();
        List<LikeView> likes = service.getMinePerPage(ev, page);

        //ログイン中の従業員が作成したいいねデータの件数を取得
        long likeReportsCount = service.countAllMine(ev);

        putRequestScope(AttributeConst.LIKES, likes); //取得したいいねデータ
        putRequestScope(AttributeConst.LIK_COUNT_MINE, likeReportsCount); //ログイン中の従業員が作成したいいねの数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

      //一覧画面を表示
        forward(ForwardConst.FW_LIK_INDEX);
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
        //セッションに登録完了のフラッシュメッセージを設定
        putSessionScope(AttributeConst.FLUSH, MessageConst.I_LIKED.getMessage());

        response.sendRedirect(request.getContextPath() + "/?action=" + ForwardConst.ACT_REP.getValue() + "&command=" + ForwardConst.CMD_SHOW.getValue() + "&id=" + toNumber(getRequestParam(AttributeConst.REP_ID)));
    }

    public void destroy() throws ServletException, IOException {
        //セッションからログイン中の従業員情報を取得
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);
        //idを条件に日報データを取得する
        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        Like l = service.getLikeReport(ev, rv);

        service.destroy(l);

        //セッションに登録完了のフラッシュメッセージを設定
        putSessionScope(AttributeConst.FLUSH, MessageConst.I_LIKE_DELETED.getMessage());

        response.sendRedirect(request.getContextPath() + "/?action=" + ForwardConst.ACT_REP.getValue() + "&command=" + ForwardConst.CMD_SHOW.getValue() + "&id=" + toNumber(getRequestParam(AttributeConst.REP_ID)));
    }
}
