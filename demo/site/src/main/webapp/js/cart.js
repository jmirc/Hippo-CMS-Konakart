$(document).ready(function(){
    $(".rate").children("li").click(
        function () {
            $(this).addClass("on");
            $(this).prevAll().addClass("on");
            $(this).nextAll().removeClass("on");
            $("#ratingField").val($(this).text());
        }),
        $(".rate").children("li").hover(
            function () {
                $(this).addClass("hover");
                $(this).prevAll().addClass("hover-on");
                $(this).nextAll().addClass("hover-off");
            },
            function () {
                $(this).siblings().add(this).removeClass("hover hover-on hover-off");
            });


    $('#productDetailForm').submit(function(){ //listen for submit event
        $.each(params, function(i,param){
            $('<input />').attr('type', 'hidden')
                .attr('name', param.name)
                .attr('value', param.value)
                .appendTo('#productDetailForm');
        });

        return true;
    });
});
