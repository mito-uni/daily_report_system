package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import services.ReportService;

/**
 * 承認に関する処理を行うActionクラス
 *
 */
public class ApprovalAction extends ActionBase {

    private ReportService service;

    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {

        service = new ReportService();

        //メソッドを実行
        invoke();
        service.close();
    }

    /**
     * 自身の部下を一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {

        //セッションからログイン中の従業員情報を取得
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        int adminFlag = ev.getAdminFlag();

        //指定されたページ数の一覧画面に表示する日報データを取得
        int page = getPage();

        if (adminFlag == 3) {
            List<EmployeeView> employees = service.getRoleDirectorPerPage(page);
            putRequestScope(AttributeConst.EMPLOYEES, employees); //取得した従業員データ
        } else if(adminFlag == 2) {
            List<EmployeeView> employees = service.getRoleManagerPerPage(page);
            putRequestScope(AttributeConst.EMPLOYEES, employees); //取得した従業員データ
        } else {
            List<EmployeeView> employees = null;
            putRequestScope(AttributeConst.EMPLOYEES, employees); //取得した従業員データ
        }

        //全日報データの件数を取得
        // long employeesCount = service.countAll();

        // putRequestScope(AttributeConst.EMP_COUNT, employeesCount); //従業員データの件数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_APRL_INDEX);
    }

    /**
     * 詳細画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException {
    }

    /**
     * 編集画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void edit() throws ServletException, IOException {

    }

    /**
     * 更新を行う
     * @throws ServletException
     * @throws IOException
     */
    public void update() throws ServletException, IOException {

    }

}