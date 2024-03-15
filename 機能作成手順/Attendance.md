# 日報管理システムの日報に勤怠時間項目の追加

##作業内容

1. モデルに勤怠カラムの追加
2. 日報の新規登録ビューの追加、アクションの追加
3. 日報詳細画面のビュー表示の変更
4. 日報更新アクションの追加

### １．モデルに勤怠カラムの追加

・Report.javaに始業時間(started_at)と終業時間(closed_at)の項目を追加。
(定数定義をしているJpaConst.javaに定数を追加済み)

src/main/java/models/Report.java

```
public class Report {
:
:
//始業時間
@Column(name = JpaConst.REP_COL_STARTED_AT, nullable = false)
private LocalDateTime startedAt;

//終業時間
@Column(name = JpaConst.REP_COL_CLOSED_AT, nullable = false)
private LocalDateTime closedAt;
:
:
}
```

・RrportView.javaとReportConverter.javaにも始業時間と終業時間の項目を追加。


### ２．日報の新規登録アクションの追加

・日報の新規登録画面に時刻入力の記述を追加
src/main/webapp/WEB-INF/views/reports/_form.jsp

```
:
:
<fmt:parseDate value="${report.startedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="startDay" type="date" />
<label for="${AttributeConst.REP_STARTED_AT.getValue()}">始業時間</label><br />
<input type="time" name="${AttributeConst.REP_STARTED_AT.getValue()}" id="${AttributeConst.REP_STARTED_AT.getValue()}" value="<fmt:formatDate value='${startDay}' pattern='HH:mm' />" />
<br /><br />

<fmt:parseDate value="${report.closedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="closeDay" type="date" />
<label for ="${AttributeConst.REP_CLOSED_AT.getValue()}">終業時間</label><br />
<input type="time" name="${AttributeConst.REP_CLOSED_AT.getValue()}" id="${AttributeConst.REP_CLOSED_AT.getValue()}" value="<fmt:formatDate value='${closeDay}' pattern='HH:mm' />" />
<br /><br />
:
:
```

・ReportActionにViewからの入力情報を取得し、インスタンスを作成する記述を追加する。
LocalTimeで取得し、ReportDateと合わせて、LocalDateTime型に変換する。

src/main/java/actions/ReportAction.java

```
public void create() throws ServletException, IOException {
：
：
//セッションから入力した勤怠の時間を取得、入力されていなければ、00:00を設定。
LocalTime startTime = null;
if (getRequestParam(AttributeConst.REP_STARTED_AT) == null
        || getRequestParam(AttributeConst.REP_STARTED_AT).equals("")) {
    startTime = LocalTime.of(0, 0, 0);
} else {
   startTime = LocalTime.parse(getRequestParam(AttributeConst.REP_STARTED_AT));
}
//セッションから入力した勤怠の時間を取得、入力されていなければ、00:00を設定。
LocalTime closeTime = null;
if (getRequestParam(AttributeConst.REP_CLOSED_AT) == null
       || getRequestParam(AttributeConst.REP_CLOSED_AT).equals("")) {
   closeTime = LocalTime.of(0, 0, 0);
} else {
   closeTime = LocalTime.parse(getRequestParam(AttributeConst.REP_CLOSED_AT));
}

//パラメータの値をもとに日報情報のインスタンスを作成する
ReportView rv = new ReportView(
       null,
       ev, //ログインしている従業員を、日報作成者として登録する
       day,
       getRequestParam(AttributeConst.REP_TITLE),
       getRequestParam(AttributeConst.REP_CONTENT),
       LocalDateTime.of(day, startTime),
       LocalDateTime.of(day, closeTime),
       null,
       null);
:
:
}
```

### ３．日報詳細画面のビュー表示の変更

src/main/webapp/WEB-INF/views/reports/show.jsp

```
:
:
<tr>
   <th>始業時間</th>
   <fmt:parseDate value="${report.startedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="startDay" type="date" />
   <td><fmt:formatDate value="${startDay}" pattern="HH:mm" /></td>
</tr>
<tr>
   <th>終業時間</th>
   <fmt:parseDate value="${report.closedAt}" pattern="yyyy-MM-dd'T'HH:mm" var="closeDay" type="date" />
   <td><fmt:formatDate value="${closeDay}" pattern="HH:mm" /></td>
</tr>
:
:
```

### ４．日報更新アクションの追加

・ActionBaseにtoLocalTime()メソッドの定義

src/main/java/actions/ActionBase.java

```
:
:
/**
 * 文字列をLocalTime型に変換する
 * @param strDate 変換前文字列
 * @return 変換後LocalTimeインスタンス
 */
protected LocalTime toLocalTime(String strDate) {
   if (strDate == null || strDate.equals("")) {
       return LocalTime.of(0, 0, 0);
   }
   return LocalTime.parse(strDate);
}
:
:
```

・更新アクションに勤怠時間の項目を追加

src/main/java/actions/ReportAction.java

```
:
:
LocalDate day = toLocalDate(getRequestParam(AttributeConst.REP_DATE));
//セッションから入力した勤怠の時間を取得、入力されていなければ、00:00を設定。
LocalTime startTime = toLocalTime(getRequestParam(AttributeConst.REP_STARTED_AT));
LocalTime closeTime = toLocalTime(getRequestParam(AttributeConst.REP_CLOSED_AT));

//入力された日報内容を設定する
rv.setReportDate(day)
rv.setStartedAt(LocalDateTime.of(day, startTime));
rv.setClosedAt(LocalDateTime.of(day, closeTime));
:
:
```

以上。
