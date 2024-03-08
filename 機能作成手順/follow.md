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

#### DTOモデルの作成(/models/Follow.java)
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

#### Viewモデルの作成(/action/views/FollowView.java)
ソースコード[/action/views/FollowView.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/views/FollowView.java)

View表示用のモデルを作成、今回はテーブル内の値と変化がないため、特に必要性はないが、理解を深めるために作成。

#### コンバーターの作成(/action/view/FollowConverter.java)
ソースコード[/action/view/FollowConverter.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/views/FollowConverter.java)

DTOモデル⇔Viewモデルの変換を行うコンバーターを作成。

#### テーブル操作用クラスの作成(/services/FollowService.java)
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

#### アクションの作成(/actions/FollowAction.java)
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

#### ビューの作成(/webappファイル)
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