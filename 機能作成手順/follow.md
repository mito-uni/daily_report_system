# フォロー機能について

### 作業内容

1.フォロー作成機能追加
2.フォロー済みの場合、フォロー済みと表示するようにビューを変更
3.フォローしている従業員を一覧で表示、フォローしている従業員の日報を一覧表示
4.フォロー削除機能追加

### 1.フォロー作成機能追加
- 使用する定数の定義追加(/constantsファイル)
- DTOモデルの作成(/models/Follow.java)
- Viewモデルの作成(/action.views/FollowView.java)
- コンバーターの作成(/action.view/FollowConverter.java)
- テーブル操作用クラスの作成(/services/FollowService.java)
- アクションの作成(/actions/FollowAction.java)
- ビューの作成(/webappファイル)

## 使用する定数の定義追加(/constantsファイル)

### DTOモデルの作成(/models/Follow.java)
ソースコード[models/Follow.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/models/Follow.java)

フォローテーブルを作成するためFollow.javaを作成。

従業員同士の多対多の関係のため、外部キーカラムを2つ定義する。

```
(省略)
    //フォローした従業員
    @ManyToOne
    @JoinColumn(name = JpaConst.FOL_COL_FOLLOWING, nullable = false)
    private Employee following;

    //フォローされた従業員
    @ManyToOne
    @JoinColumn(name = JpaConst.FOL_COL_FOLLOWED, nullable = false)
    private Employee followed;
(省略)
```

### Viewモデルの作成(/action/views/FollowView.java)
ソースコード[/action/views/FollowView.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/views/FollowView.java)

View表示用のモデルを作成、今回はテーブル内の値と変化がないため、特に必要性はないが、理解を深めるために作成。

### コンバーターの作成(/action/view/FollowConverter.java)
ソースコード[/action/view/FollowConverter.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/views/FollowConverter.java)

DTOモデル⇔Viewモデルの変換を行うコンバーターを作成。

### テーブル操作用クラスの作成(/services/FollowService.java)
ソースコード[/services/FollowService.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/services/FollowService.java)

指定したidの情報を取得するメソッドと、指定したFollowViewを渡すとfollowsレコードを作成する記述を追加。

```
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
```

### アクションの作成(/actions/FollowAction.java)
ソースコード[/actions/FollowAction.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/FollowAction.java)

ログイン中の従業員情報とフォローする従業員情報を取得し、followsレコードを作成する記述を追加。

```
public class FollowAction extends ActionBase {
    private FollowService service;
(省略)
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
```

### ビューの作成(/webappファイル)
ソースコード[/webapp/WEB-INF/views/reports/show.jsp](https://github.com/mito-uni/daily_report_system/blob/main/src/main/webapp/WEB-INF/views/reports/show.jsp)

フォローボタンをreports/show.jspに記述追加。

```
(省略)
        <c:if test="${sessionScope.login_employee.id != report.employee.id}">
            <div>
                <form method="POST" action="<c:url value='?action=${actFol}&command=${commCrt}' />">
                    <input type="hidden" name="${AttributeConst.EMP_ID.getValue()}" value="${report.employee.id}" />
                    <button type="submit">${report.employee.name}さんをフォローする</button>
                </form>
            </div><br />
(省略)
```

## 2.フォロー済みの場合、フォロー済みと表示するようにビューを変更

- 使用する定数の定義追加(/constantsファイル)
- DTOモデルに記述追加(/models/Report.java)
- テーブル操作用クラスに記述追加(/services/ReportService.java)
- アクションに記述追加(/actions/ReportAction.java)
- ビューに記述追加(/webappファイル)

### 使用する定数の定義追加(/constantsファイル)
ソースコード[/constants/AttributeConst.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/constants/AttributeConst.java)

ソースコード[/constants/JpaConst.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/constants/JpaConst.java)

指定した従業員(followingとfollowed)のレコード件数を取得する記述を追加。

```
    //指定した従業員が指定の従業員をフォローしている件数を取得(フォローしているかの確認)
    String Q_FOL_COUNT_FOL_MINE = ENTITY_FOL + ".countFolMine";
    String Q_FOL_COUNT_FOL_MINE_DEF = "SELECT COUNT(f) FROM Follow As f WHERE f.following = :" + JPQL_PARM_FOLLOWING + " AND f.followed = :" + JPQL_PARM_FOLLOWED;
```

### DTOモデルに記述追加(/models/Report.java)
ソースコード[/models/Report.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/models/Report.java)

```
    @NamedQuery(
            name = JpaConst.Q_FOL_COUNT_FOL_MINE,
            query = JpaConst.Q_FOL_COUNT_FOL_MINE_DEF)
```

### テーブル操作用クラスに記述追加(/services/ReportService.java)
ソースコード[/services/ReportService.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/services/ReportService.java)

idを条件にEmployeeViewを取得する記述と、followingとfollowedの組み合わせが等しいレコードの件数を取得する記述を追加。

```
(省略)
    /**
     * idを条件に取得したデータをEmployeeViewのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    public EmployeeView findEmployee(int id) {
        return EmployeeConverter.toView(findEmployeeInternal(id));
    }
(省略)
    /*
     * 指定した従業員が指定した従業員にフォローされている件数を取得し、返却する
     */
    public long countFollowMine(EmployeeView following, EmployeeView followed) {
        long follows_count_mine = (long) em.createNamedQuery(JpaConst.Q_FOL_COUNT_FOL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_FOLLOWING, EmployeeConverter.toModel(following))
                .setParameter(JpaConst.JPQL_PARM_FOLLOWED, EmployeeConverter.toModel(followed))
                .getSingleResult();
        return follows_count_mine;
    }

(省略)
    /**
     * idを条件にデータを1件取得する
     * @param id
     * @return 取得データのインスタンス
     */
    private Employee findEmployeeInternal(int id) {
        return em.find(Employee.class, id);
    }
(省略)
```

### アクションに記述追加(/actions/ReportAction.java)
ソースコード[/actions/ReportAction.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/ReportAction.java)

ReportServiceで定義したメソッドを使用し、フォローレコードの件数を取得。

```
    /**
     * 詳細画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException {
        //セッションからログイン中の従業員情報を取得
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        //idを条件に日報データを取得する
        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

(省略)
        if (rv == null) {
            //該当の日報データが存在しない場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);

        } else {

            putRequestScope(AttributeConst.REPORT, rv); //取得した日報データ

            //idを条件に従業員データを取得
            EmployeeView employee = service.findEmployee((rv.getEmployee().getId()));

            //ログイン中の従業員が指定した従業員にフォローした件数を取得
            long followsCountMine = service.countFollowMine(ev, employee);
            putRequestScope(AttributeConst.FOL_COUNT_MINE, followsCountMine);

            //詳細画面を表示
            forward(ForwardConst.FW_REP_SHOW);
        }
    }
```

### ビューに記述追加(/webappファイル)
ソースコード[/webapp/WEB-INF/views/reports/show.jsp](https://github.com/mito-uni/daily_report_system/blob/main/src/main/webapp/WEB-INF/views/reports/show.jsp)

フォローしている場合（followsテーブルにレコードが存在する場合)、フォロー済みを表示させる。

```
        <c:if test="${sessionScope.login_employee.id != report.employee.id}">
            <div>
                <c:choose>
                    <c:when test="${follows_count_mine == 0}">
                        <form method="POST" action="<c:url value='?action=${actFol}&command=${commCrt}' />">
                            <button type="submit">${report.employee.name}さんをフォローする</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                       <p>フォロー済み</p>
                    </c:otherwise>
                </c:choose>
            </div><br />
(省略)
```

## 3.フォローしている従業員を一覧で表示、フォローしている従業員の日報を一覧表示

- 使用する定数の定義追加(/constantsファイル)
- DTOモデルに記述追加(/models/Report.java)
- テーブル操作用クラスに記述追加(/services/ReportService.java)
- アクションに記述追加(/actions/ReportAction.java)
- ビューに記述追加(/webappファイル)

### 使用する定数の定義追加(/constantsファイル)
ソースコード[]()

### DTOモデルに記述追加(/models/Report.java)
ソースコード[]()

### テーブル操作用クラスに記述追加(/services/ReportService.java)
ソースコード[]()

### アクションに記述追加(/actions/ReportAction.java)
ソースコード[]()

### ビューに記述追加(/webappファイル)
ソースコード[]()
