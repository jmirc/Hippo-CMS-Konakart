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
});
