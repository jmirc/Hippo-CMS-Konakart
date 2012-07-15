<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="headTitle" type="java.lang.String"--%>
<%--@elvariable id="product" type="org.onehippo.forge.konakart.hst.beans.KKProductDocument"--%>

<hst:headContribution category="scripts">
    <hst:link var="carouselJs" path="/libs/bootstrap/js/caroussel.js"/>
    <script src="${carouselJs}" type="text/javascript"></script>
</hst:headContribution>

<script type="text/javascript">
    $(document).ready(function () {
        $('.carousel').carousel({
            interval:2000
        });
    })
</script>


<div class="span9">
    <div id="myCarousel" class="carousel slide">
        <div class="carousel-inner">
            <div class="item active">
                <img src="<hst:link path="/css/images/carousel_1.jpg"/>" alt="">

                <div class="carousel-caption">
                    <h4>First Thumbnail label</h4>

                    <p>Cras justo odio, dapibus ac facilisis in, egestas eget quam. Donec id elit non mi porta gravida
                        at eget metus. Nullam id dolor id nibh ultricies vehicula ut id elit.</p>
                </div>

            </div>
            <div class="item">
                <img src="<hst:link path="/css/images/carousel_2.jpg"/>" alt="">

                <div class="carousel-caption">
                    <h4>Second Thumbnail label</h4>

                    <p>Cras justo odio, dapibus ac facilisis in, egestas eget quam. Donec id elit non mi porta gravida
                        at eget metus. Nullam id dolor id nibh ultricies vehicula ut id elit.</p>
                </div>
            </div>

            <div class="item">
                <img src="<hst:link path="/css/images/carousel_3.jpg"/>" alt="">

                <div class="carousel-caption">
                    <h4>Third Thumbnail label</h4>

                    <p>Cras justo odio, dapibus ac facilisis in, egestas eget quam. Donec id elit non mi porta gravida
                        at eget metus. Nullam id dolor id nibh ultricies vehicula ut id elit.</p>
                </div>
            </div>
        </div>

        <a class="left carousel-control" href="#myCarousel" data-slide="prev">&lsaquo;</a>
        <a class="right carousel-control" href="#myCarousel" data-slide="next">&rsaquo;</a>
    </div>
</div>

<div class="span7 popular_products">
    <h4>New products</h4><br/>
    <ul class="thumbnails">
        <c:forEach var="product" items="${products.items}">
            <hst:link var="prdImgLink" hippobean="${product.mainImage.original}"/>
            <hst:link var="prdlink" hippobean="${product}"/>
            <hst:cmseditlink hippobean="${product}"/>
            <li class="span2">
                <div class="thumbnail">
                    <a href="${prdlink}"><img alt="product.mainImage.original.name" width="150" src="${prdImgLink}"/></a>

                    <div class="caption">
                        <a href="${prdlink}"><h5>${product.productIf.name}</h5></a> Price:
                        <c:if test="${not empty product.specialPrice}"><s></c:if>
                        <kk:formatPrice price="${product.productIf.price0}"/>
                        <c:if test="${not empty product.specialPrice}"></s></c:if>
                        <c:if test="${not empty product.specialPrice}">&nbsp;|&nbsp;
                            <kk:formatPrice price="${product.specialPrice}"/>
                        </c:if>
                    </div>
                </div>
            </li>

        </c:forEach>

    </ul>
</div>
<div class="span2">

    <div class="roe">
        <h4>Newsletter</h4><br/>

        <p>Sign up for our weekly newsletter and stay up-to-date with the latest offers, and newest products.</p>

        <form class="form-search">
            <input type="text" class="span2" placeholder="Enter your email"/><br/><br/>
            <button type="submit" class="btn pull-right">Subscribe</button>
        </form>
    </div>
    <br/><br/>
    <a href="#"><img alt="" title="" src="css/images/paypal_mc_visa_amex_disc_150x139.gif"/></a>
    <a href="#"><img alt="" src="css/images/bnr_nowAccepting_150x60.gif"/></a>

</div>



