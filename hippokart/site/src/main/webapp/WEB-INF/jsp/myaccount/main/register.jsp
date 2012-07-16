<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<hst:headContribution category="css">
    <hst:link path="/libs/datepicker/css/datepicker.css" var="datepickerCss"/>
    <link rel="stylesheet" href="${datepickerCss}" type="text/css"/>
</hst:headContribution>

<hst:headContribution category="scripts">
    <hst:link path="/libs/datepicker/js/bootstrap-datepicker.js" var="datepickerJs"/>
    <script src="${datepickerJs}" type="text/javascript"></script>
</hst:headContribution>

<hst:headContribution category="scripts">
    <hst:link path="/js/myaccount.js" var="myaccountJs"/>
    <script src="${myaccountJs}" type="text/javascript"></script>
</hst:headContribution>

<hst:link var="countryDropDown" path="/restservices/checkout/states"/>

<script type="text/javascript">

    $(document).ready(function () {
        $('#CountryDropDown').change(function () {
            $.getJSON("${countryDropDown}/" + $(this).val(), "", function (data) {
                var list = $('#StateDropDown');
                list.empty('option')
                        .append($('<option />').attr('selected', 'true').text('---').val('-1'));

                if (data.length > 0) {
                    $('#StateDropDown').addClass('highlight');
                    $('#StateDropDownLabel').addClass('highlight');
                    $("#StateDropDown").rules("add", "required");
                } else {
                    $('#StateDropDown').removeClass('highlight');
                    $('#StateDropDownLabel').removeClass('highlight');
                    $("#StateDropDown").rules("remove", "required");
                }

                $.each(data, function (index, itemData) {
                    list.append($('<option />').text(itemData.name).val(itemData.name));
                });
            });
        });
    });
</script>

<ul class="breadcrumb">
    <li><a href="<hst:link path="/"/>">Home</a> <span class="divider">/</span></li>
    <li><a href="<hst:link siteMapItemRefId="myaccount"/>">My Account</a> <span class="divider">/</span></li>
    <li class="active">Register</li>
</ul>

<div class="row">
    <div class="span12">
        <h1>Create an account</h1>
        <hr />

        <hst:actionURL var="register">
            <hst:param name="action" value="CREATE_ACCOUNT"/>
        </hst:actionURL>
        <form id="registerFormID" action="${register}" class="form-horizontal" method="post">
            <fieldset>
                <tag:registerCustomer/>
            </fieldset>


        </form>

    </div>
</div>
