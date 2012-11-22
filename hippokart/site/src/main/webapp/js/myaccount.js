$(document).ready(function () {
    extra = {
        highlight: function (element, errorClass, validClass) {
            $(element).closest('.control-group').addClass("error");
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).closest('.control-group').removeClass("error");
        },
        success: function(element, errorClass, validClass) {
            $(element).text('OK!').addClass('valid')
                .closest('.control-group').addClass('success');
        }
    };

    // binds form submission and fields to the validation engine
    $("#loginFormID").validate(extra);
    $("#registerFormID").validate(extra);
    jQuery("#dateofbirth").datepicker();
});