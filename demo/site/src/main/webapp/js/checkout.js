$(document).ready(function () {
    $('#CountryDropDown').change(function () {
        $.getJSON("/YourServerHandler/GetStates", { category:$(this).val() }, function (data) {
            var list = $('#StateDropDown');
            list.empty('option');
            list.append($('<option />').attr('selected', 'true').text('---').val('-1'));
            $.each(data, function (index, itemData) {
                list.append($('<option />').text(itemData).val(itemData));
            });
        });
    });


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
