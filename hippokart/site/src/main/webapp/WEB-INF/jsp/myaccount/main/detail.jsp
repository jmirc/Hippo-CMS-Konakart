<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>

<ul class="breadcrumb">
    <li><a href="<hst:link path="/"/>">Home</a> <span class="divider">/</span></li>
    <li><a href="<hst:link siteMapItemRefId="myaccount"/>">My Account</a> <span class="divider">/</span></li>
    <li class="active">My Account Information</li>
</ul>

<div class="row">
    <div class="span12">
        <h1>My Account Information</h1>
        <hr/>

        <div class="row-fluid">
            <div class="span4">
                <h3>My Account</h3>

                <ul class="nav nav-list">
                    <li><a href="">View or change my account information.</a></li>
                    <li><a href="">View or change entries in my address book.</a></li>
                    <li><a href="">Change my account password.</a></li>
                </ul>
            </div>
            <div class="span4">
                <h3>My Orders</h3>

                <ul class="nav nav-list">
                    <li><a href="">View the orders I have made.</a></li>
                </ul>
            </div>
            <div class="span4">
                <h3>E-Mail Notifications</h3>

                <ul class="nav nav-list">
                    <li><a href="">Subscribe or unsubscribe from newsletters.</a></li>
                    <li><a href="">View or change my product notification list.</a></li>
                </ul>
            </div>
        </div>
    </div>
</div>
