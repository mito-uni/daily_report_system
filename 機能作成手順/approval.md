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