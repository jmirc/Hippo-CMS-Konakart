$(document).ready(function () {
    function changeDefaultNewAddressForm() {
        if ($("#select-address option:selected").val() == -1) {
            $("#new-address-form").show();
        } else  {
            $("#new-address-form").hide();
        }
    }
    $("#new-address-form").hide();
    $('#select-address').change(changeDefaultNewAddressForm);


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
    $("#billingFormID").validate(extra);

    $("#shippingFormID").validate(extra);

    $("#dateofbirth").datepicker();


});
