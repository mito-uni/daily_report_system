# いいね機能について

### 作業内容

1. いいね作成機能
2. 日報詳細にいいね数の表示追加
3. 日報に同じ従業員は1回のみいいねできるようにする
4. 従業員がいいねした日報を一覧で見るようにする
5. いいね削除機能

## 1.いいね作成機能

- 使用する定数の定義追加(/constantsファイル)
- DTOモデルの作成(/models/Like.java)
- Viewモデルの作成(/action.views/LikeView.java)
- コンバーターの作成(/action.view/LikeConverter.java)
- テーブル操作用クラスの作成(/services/LikeService.java)
- アクションの作成(/actions/LikeAction.java)
- ビューの作成(/webappファイル)

##### 使用する定数の定義追加(/constantsファイル)
ソースコード[/constants/ForwardConst.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/constants/ForwardConst.java)

##### DTOモデルの作成(/models/Like.java)
ソースコード[models/Like.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/models/Like.java)

従業員と日報が多対多の関係性となるため、中間テーブルとなるLikesテーブルを定義する。<br>
外部キーとして従業員idと日報idを指定する。
下記一部抜粋<br>

```
//いいねした従業員のid
    @ManyToOne
    @JoinColumn(name = JpaConst.LIK_COL_EMP, nullable = false)
    private Employee employee;

//いいねされた日報のid
    @ManyToOne
    @JoinColumn(name = JpaConst.LIK_COL_REP, nullable = false)
    private Report report;
```

##### Viewモデルの作成(/action.views/LikeView.java)
ソースコード[/action.views/LikeView.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/views/LikeView.java)

View表示用のモデルを作成、今回はテーブル内の値と変化がないため、特に必要性はないが、理解を深めるために作成。

##### コンバーターの作成(/action.view/LikeConverter.java)
ソースコード[/action.view/LikeConverter.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/views/LikeConverter.java)

DTOモデル⇔Viewモデルの変換を行うクラス

##### テーブル操作用クラスの作成(/services/LikeService.java)
ソースコード[/services/LikeService.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/services/LikeService.java)

findOne()メソッドで引数として渡したidのレポートが取得可能となる。

```
（省略)
 /**
     * idを条件に取得したデータをReportViewのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    public ReportView findOne(int id) {
        return ReportConverter.toView(findOneInternal(id));
    }
（省略)
     /**
     * idを条件にデータを1件取得する
     * @param id
     * @return 取得データのインスタンス
     */
    private Report findOneInternal(int id) {
        return em.find(Report.class, id);
    }
（省略)
```

##### アクションとビューの作成(/actions/LikeAction.java)
ソースコード[/actions/LikeAction.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/LikeAction.java)

##### ビューの作成(日報詳細画面)
ソースコード[日報詳細画面](https://github.com/mito-uni/daily_report_system/blob/main/src/main/webapp/WEB-INF/views/reports/show.jsp)

追加部分抜粋

```
<c:if test="${sessionScope.login_employee.id != report.employee.id}">
    <div>
        <form method="POST" action="<c:url value='?action=${actLik}&command=${commCrt}' />">
            <input type="hidden" name="${AttributeConst.REP_ID.getValue()}" value="${report.id}" />
                <button type="submit">いいね</button>
        </form>
    </div>
</c:if>
```

下記でログインしている従業員自身の日報には、いいねボタンを非表示にする。

```
<c:if test="${sessionScope.login_employee.id != report.employee.id}">
```

下記の記述で、ボタンを押下すると、LikeActionのcreate()メソッドが起動するようになる。
また、inputで詳細表示しているreport_idを取得できるようにした。
上部に使用した定数ファイルのimportを行うこと。

```
<form method="POST" action="<c:url value='?action=${actLik}&command=${commCrt}' />">
    <input type="hidden" name="${AttributeConst.REP_ID.getValue()}" value="${report.id}" />
        <button type="submit">いいね</button>
</form>
```

## 2. 日報詳細にいいね数の表示追加

- 使用する定数の定義追加(/constantsファイル)
- DTOモデルに記述追加(/models/Report.java)
- テーブル操作用クラスに記述追加(/services/ReportService.java)
- 日報アクションのshow()メソッドに記述追加(actions/ReportAction.java)
- ビューに記述追加(日報詳細画面)

##### 使用する定数の定義追加(/constantsファイル)
ソースコード[/constants/AttributeConst.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/constants/AttributeConst.java)

画面の項目値等を定義するための記述追加。

```
(省略)
    //いいね管理
    LIKE("like"),
    LIKES("likes"),
    LIK_COUNT("likes_count"),
    LIK_ID("id");
(省略)
```

ソースコード[/constants/JpaConst.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/constants/JpaConst.java)

DB操作の記述追加。指定した日報のいいね件数を取得する。

```
(省略)
    String JPQL_PARM_REPORT = "report"; //日報
(省略)
    //指定した日報のいいね数を取得する
    String Q_LIK_COUNT_REP = ENTITY_LIK + ".countLike";
    String Q_LIK_COUNT_REP_DEF = "SELECT COUNT(l) FROM Like As l WHERE l.report = :" + JPQL_PARM_REPORT;
(省略)
```

##### DTOモデルに記述追加(/models/Report.java)
ソースコード[/models/Report.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/models/Report.java)
データベース操作の記述追加(下記追加部分抜粋)

```
(省略)
@NamedQueries({
    (省略)
     @NamedQuery(
            name = JpaConst.Q_LIK_COUNT_REP,
            query = JpaConst.Q_LIK_COUNT_REP_DEF)
})
(省略)
```

##### テーブル操作用クラスに記述追加(/services/ReportService.java)
ソースコード[/services/ReportService.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/services/ReportService.java)

指定した日報からいいね件数を取得し、返却するcountLike()メソッドを定義。

```
(省略)
    /**
     * 指定した日報のいいね数を取得し、返却する
     * @param report
     * @return 日報データのいいね数
     */
    public long countLike(ReportView report) {

        long likes_count = (long) em.createNamedQuery(JpaConst.Q_LIK_COUNT_REP, Long.class)
                .setParameter(JpaConst.JPQL_PARM_REPORT, ReportConverter.toModel(report))
                .getSingleResult();

        return likes_count;
    }
(省略)
```

##### 日報アクションのshow()メソッドに記述追加(actions/ReportAction.java)
ソースコード[actions/ReportAction.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/ReportAction.java)

いいね件数取得の記述を追加。

指定した定数にいいね件数の値を定義。

```
public void show() throws ServletException, IOException {
    (省略)
        //idを条件に日報データを取得する
        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        //指定した日報のいいね件数を取得
        long likesCount = service.countLike(rv);
        putRequestScope(AttributeConst.LIK_COUNT, likesCount); //日報のいいね数
    (省略)
}
```

##### ビューに記述追加(日報詳細画面)
ソースコード[日報詳細画面](https://github.com/mito-uni/daily_report_system/blob/main/src/main/webapp/WEB-INF/views/reports/show.jsp)

いいね件数の表示追加

```
(省略)
<tr>
    <th>いいね</th>
    <td>${likes_count}</td>
</tr>
(省略)
```