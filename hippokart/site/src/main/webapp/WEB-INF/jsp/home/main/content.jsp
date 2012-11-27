<%@ include file="/WEB-INF/jspf/htmlTags.jspf" %>
<%--@elvariable id="headTitle" type="java.lang.String"--%>
<%--@elvariable id="product" type="com.konakart.app.Product"--%>

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
                <img src="<hst:link path="/images/magna-plaza-shopping.png"/>" alt="">

                <div class="carousel-caption">
                    <h4>New Record: World's Largest Wind Turbine</h4>

                    <p><br/>&nbsp;</p>
                </div>

            </div>
            <div class="item">
                <img src="<hst:link path="/images/organic-lunch-bag-prints.png"/>" alt="">

                <div class="carousel-caption">
                    <h4>Solar power: the sky is the limit</h4>

                    <p><br/>&nbsp;</p>
                </div>
            </div>

            <div class="item">
                <img src="<hst:link path="/images/solar-panels.png"/>" alt="">

                <div class="carousel-caption">
                    <h4>European Retailers Adopt Voluntary <br/> Sustainable Business Code</h4>

                    <p>&nbsp;</p>
                </div>
            </div>

            <div class="item">
                <img src="<hst:link path="/images/wind-turbines.png"/>" alt="">

                <div class="carousel-caption">
                    <h4>Organic Cotton Reusable Lunch Bag</h4>

                    <p><br/>&nbsp;</p>
                </div>
            </div>

            <div class="item">
                <img src="<hst:link path="/images/gree-home-and-living-show.png"/>" alt="">

                <div class="carousel-caption">
                    <h4>Rhode Island Green Home and Living Show</h4>

                    <p><br/>&nbsp;</p>
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
            <kk:retrieveKKProductDocument productId="${product.id}" var="kkDocument"/>

            <hst:link var="prdImgLink" hippobean="${kkDocument.mainImage.original}"/>
            <hst:link var="prdlink" hippobean="${kkDocument}"/>
            <hst:cmseditlink hippobean="${kkDocument}"/>
            <li class="span2">
                <div class="thumbnail">
                    <a href="${prdlink}"><img alt="${kkDocument.mainImage.original.name}" width="150" src="${prdImgLink}"/></a>

                    <div class="caption">
                        <a href="${prdlink}"><h5>${product.name}</h5></a> Price:
                        <c:if test="${not empty kkDocument.specialPrice}"><s></c:if>
                        <kk:formatPrice price="${product.price0}"/>
                        <c:if test="${not empty kkDocument.specialPrice}"></s></c:if>
                        <c:if test="${not empty kkDocument.specialPrice}">&nbsp;|&nbsp;
                            <kk:formatPrice price="${kkDocument.specialPrice}"/>
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
    <a href="#"><img alt="" title="" src="<hst:link path="/images/paypal_mc_visa_amex_disc_150x139.gif"/>"/></a>
    <a href="#"><img alt="" src="<hst:link path="/images/bnr_nowAccepting_150x60.gif"/>"/></a>

</div>



