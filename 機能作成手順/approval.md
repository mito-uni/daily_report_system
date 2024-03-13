# 課長や部長等の権限の追加・および部下の日報を承認する機能

1. Employeeモデルに課長や部長の権限追加。
2. Reportモデルに承認済み・承認待ち・却下の選択リストとコメント欄を追加。

やりたいこと

1. 従業員（すべての従業員）が日報を作成する。
2. 日報が承認待ち状態となり、自身と自身の上長のみが日報を閲覧することが可能。
3. 上長が承認、または、却下を選択し、コメントを記述。
4. 承認であれば、承認済みとして、全体に日報が閲覧可能となる。
5. 却下であれば、自身の却下された日報として、コメントを読み取り、編集再度、承認に送る。
6. 手順４から繰り返し。


## 1. Employeeモデルに課長や部長の権限追加

- 権限の定数定義を追加。
- EmployeeConverterの記述追加。
- Viewの記述追加。


### 権限の定数定義を追加

AttributeConst.javaに追加

```
    ROLE_DIRECTOR(3),
    ROLE_MANAGER(2),
```

JpaConst.javaに追加

```
    int ROLE_DIRECTOR = 3; //部長権限ON(部長)
    int ROLE_MANAGER = 2; //課長権限ON(課長)
```


### EmployeeConverterの記述追加。

EmployeeConverterにAdminFlagの変換記述を追加。

```
/**
 * 従業員データのDTOモデル⇔Viewモデルの変換を行うクラス
 *
 */
public class EmployeeConverter {

    /**
     * ViewモデルのインスタンスからDTOモデルのインスタンスを作成する
     * @param ev EmployeeViewのインスタンス
     * @return Employeeのインスタンス
     */
    public static Employee toModel(EmployeeView ev) {

        return new Employee(
                (省略)
                ev.getAdminFlag() == null
                        ? null
                        : ev.getAdminFlag() == AttributeConst.ROLE_ADMIN.getIntegerValue()
                                ? JpaConst.ROLE_ADMIN
                                : ev.getAdminFlag() == AttributeConst.ROLE_MANAGER.getIntegerValue()
                                        ? JpaConst.ROLE_MANAGER
                                        : ev.getAdminFlag() == AttributeConst.ROLE_DIRECTOR.getIntegerValue()
                                                ? JpaConst.ROLE_DIRECTOR
                                                : JpaConst.ROLE_GENERAL,
                (省略)
    }


    /**
     * DTOモデルのインスタンスからViewモデルのインスタンスを作成する
     * @param e Employeeのインスタンス
     * @return EmployeeViewのインスタンス
     */
    public static EmployeeView toView(Employee e) {

        if(e == null) {
            return null;
        }

        return new EmployeeView(
                (省略)
                e.getAdminFlag() == null
                        ? null
                        : e.getAdminFlag() == JpaConst.ROLE_ADMIN
                                ? AttributeConst.ROLE_ADMIN.getIntegerValue()
                                : e.getAdminFlag() == JpaConst.ROLE_MANAGER
                                        ? AttributeConst.ROLE_MANAGER.getIntegerValue()
                                        : e.getAdminFlag() == JpaConst.ROLE_DIRECTOR
                                                ? AttributeConst.ROLE_DIRECTOR.getIntegerValue()
                                                : AttributeConst.ROLE_GENERAL.getIntegerValue(),
                (省略)
    }

```

### Viewの記述追加。

reports/_form.jspに権限表示を追加。

```
<label for="${AttributeConst.EMP_ADMIN_FLG.getValue()}">権限</label><br />
<select name="${AttributeConst.EMP_ADMIN_FLG.getValue()}" id="${AttributeConst.EMP_ADMIN_FLG.getValue()}">
    <option value="${AttributeConst.ROLE_GENERAL.getIntegerValue()}"<c:if test="${employee.adminFlag == AttributeConst.ROLE_GENERAL.getIntegerValue()}"> selected</c:if>>一般</option>
    <option value="${AttributeConst.ROLE_ADMIN.getIntegerValue()}"<c:if test="${employee.adminFlag == AttributeConst.ROLE_ADMIN.getIntegerValue()}"> selected</c:if>>管理者</option>
    <option value="${AttributeConst.ROLE_MANAGER.getIntegerValue()}"<c:if test="${employee.adminFlag == AttributeConst.ROLE_MANAGER.getIntegerValue()}"> selected</c:if>>課長</option>
    <option value="${AttributeConst.ROLE_DIRECTOR.getIntegerValue()}"<c:if test="${employee.adminFlag == AttributeConst.ROLE_DIRECTOR.getIntegerValue()}"> selected</c:if>>部長</option>
</select>
```


reports/show.jspに権限表示の追加。

```
                <tr>
                    <th>権限</th>
                    <td><c:choose>
                            <c:when test="${employee.adminFlag == AttributeConst.ROLE_ADMIN.getIntegerValue()}">管理者</c:when>
                            <c:when test="${employee.adminFlag == AttributeConst.ROLE_MANAGER.getIntegerValue()}">課長</c:when>
                            <c:when test="${employee.adminFlag == AttributeConst.ROLE_DIRECTOR.getIntegerValue()}">部長</c:when>
                            <c:otherwise>一般</c:otherwise>
                        </c:choose></td>
                </tr>
```

## 2. Reportモデルに承認済み・承認待ち・却下の選択リストとコメント欄を追加。

- 定数定義ファイルに記述を追加
- Reportモデル、ReportViewモデル、ReportConverterに記述の追加
- ReportActionに記述追加
- Viewに記述追加

### 定数定義ファイルに記述を追加

[/constants/AttributeConst.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/constants/AttributeConst.java)

```
    //日報管理
    (省略)
    REP_APPROVAL_FLAG("approval_flag"),
    REP_COMMENT("comment"),

    //承認フラグ
    APPROVAL_FLAG_FALSE(2),
    APPROVAL_FLAG_TRUE(1),
    APPROVAL_FLAG_WAIT(0),
```

[/constants/JpaConst.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/constants/JpaConst.java)

```
    //日報テーブルカラム
    (省略)
    String REP_COL_APPROVAL_FLAG = "approval_flag"; //承認フラグ
    String REP_COL_COMMENT ="comment"; //コメント

    int APPROVAL_FLAG_FALSE = 2; //承認フラグ(否認)
    int APPROVAL_FLAG_TRUE = 1; //承認フラグ(承認済み)
    int APPROVAL_FLAG_WAIT = 0; //承認フラグ(承認待ち)

```

### Reportモデル、ReportViewモデル、ReportConverterに記述の追加

[/models/Report.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/models/Report.java)

```
    //承認フラグ
    @Column(name = JpaConst.REP_COL_APPROVAL_FLAG, nullable = false)
    private Integer approvalFlag;

    //コメント
    @Lob
    @Column(name = JpaConst.REP_COL_COMMENT)
    private String comment;
```

[/actions/views/ReportView.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/views/ReportView.java)

```
public class ReportView {
(省略)
    //承認フラグ
    private Integer approvalFlag;

    //コメント
    private String comment;
}
```

[/actions/views/ReportConverter.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/views/ReportConverter.java)

```
public class ReportConverter {
    /**
     * ViewモデルのインスタンスからDTOモデルのインスタンスを作成する
     * @param rv ReportViewのインスタンス
     * @return Reportのインスタンス
     */
    public static Report toModel(ReportView rv) {
        return new Report(
                (省略)
                rv.getApprovalFlag() == null
                    ? null
                    : rv.getApprovalFlag() == AttributeConst.APPROVAL_FLAG_FALSE.getIntegerValue()
                        ? JpaConst.APPROVAL_FLAG_FALSE
                        : rv.getApprovalFlag() == AttributeConst.APPROVAL_FLAG_TRUE.getIntegerValue()
                            ? JpaConst.APPROVAL_FLAG_TRUE
                            : JpaConst.APPROVAL_FLAG_WAIT,
                rv.getComment());
    }

    /**
     * DTOモデルのインスタンスからViewモデルのインスタンスを作成する
     * @param r Reportのインスタンス
     * @return ReportViewのインスタンス
     */
    public static ReportView toView(Report r) {

        if (r == null) {
            return null;
        }

        return new ReportView(
                (省略)
                r.getApprovalFlag() == null
                    ? null
                    : r.getApprovalFlag() == JpaConst.APPROVAL_FLAG_FALSE
                        ? AttributeConst.APPROVAL_FLAG_FALSE.getIntegerValue()
                        : r.getApprovalFlag() == JpaConst.APPROVAL_FLAG_TRUE
                            ? AttributeConst.APPROVAL_FLAG_TRUE.getIntegerValue()
                            : AttributeConst.APPROVAL_FLAG_WAIT.getIntegerValue(),
                r.getComment());
    }
    (省略)
    /**
     * Viewモデルの全フィールドの内容をDTOモデルのフィールドにコピーする
     * @param r DTOモデル(コピー先)
     * @param rv Viewモデル(コピー元)
     */
    public static void copyViewToModel(Report r, ReportView rv) {
        (省略)
        r.setApprovalFlag(rv.getApprovalFlag());
        r.setComment(rv.getComment());

    }
```

### ReportActionに記述追加
[/actions/ReportAction.java](https://github.com/mito-uni/daily_report_system/blob/main/src/main/java/actions/ReportAction.java)

create()アクションの記述に承認フラグとコメントを追加。

```
    public void create() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {
            (省略)
            //パラメータの値をもとに日報情報のインスタンスを作成する
            ReportView rv = new ReportView(
                    (省略)
                    AttributeConst.APPROVAL_FLAG_WAIT.getIntegerValue(),
                    getRequestParam(AttributeConst.REP_COMMENT));
            (省略)
        }
    }
```

edit()アクションの表示条件に上位権限の追加。

```
    public void edit() throws ServletException, IOException {

        if (ev.getId() == rv.getEmployee().getId() || ev.getAdminFlag() > rv.getEmployee().getAdminFlag()) {
            //ログインしている従業員が日報の作成者、または
            //ログインしている従業員より権限が高い場合は編集画面を表示

            putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
            putRequestScope(AttributeConst.REPORT, rv); //取得した日報データ

            //編集画面を表示
            forward(ForwardConst.FW_REP_EDIT);

        } else {

            forward(ForwardConst.FW_ERR_UNKNOWN);
        }
    }
```

update()アクションに承認フラグとコメントを追加。

```
    public void update() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {
            (省略)
            rv.setApprovalFlag(toNumber(getRequestParam(AttributeConst.REP_APPROVAL_FLAG)));
            rv.setComment(getRequestParam(AttributeConst.REP_COMMENT));
            (省略)
            }
        }
    }
}
```

### Viewに記述追加

[/webapp/WEB-INF/views/reports/_form.jsp](https://github.com/mito-uni/daily_report_system/blob/main/src/main/webapp/WEB-INF/views/reports/_form.jsp)

```
<label for="${AttributeConst.REP_APPROVAL_FLAG.getValue()}">承認状態</label><br />
<select name="${AttributeConst.REP_APPROVAL_FLAG.getValue()}" id="${AttributeConst.REP_APPROVAL_FLAG.getValue()}">
    <option value="${AttributeConst.APPROVAL_FLAG_WAIT.getIntegerValue()}"<c:if test="${report.approvalFlag == AttributeConst.APPROVAL_FLAG_WAIT.getIntegerValue()}"> selected</c:if>>承認待ち</option>
    <option value="${AttributeConst.APPROVAL_FLAG_TRUE.getIntegerValue()}"<c:if test="${report.approvalFlag == AttributeConst.APPROVAL_FLAG_TRUE.getIntegerValue()}"> selected</c:if>>承認済み</option>
    <option value="${AttributeConst.APPROVAL_FLAG_FALSE.getIntegerValue()}"<c:if test="${report.approvalFlag == AttributeConst.APPROVAL_FLAG_FALSE.getIntegerValue()}"> selected</c:if>>否認</option>
</select>
<br /><br />

<label for="${AttributeConst.REP_COMMENT.getValue()}">コメント</label><br />
<textarea  name="${AttributeConst.REP_COMMENT.getValue()}" id="${AttributeConst.REP_COMMENT.getValue()}" rows="10" cols="50">${report.comment}</textarea>
<br /><br />
```