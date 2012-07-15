$(document).ready(function(){
    $('#wishlistLink').click(function() {
        $('#addToWishList').val("true");
        $('#productForm').submit();
    });
    $('#compareLink').click(function() {
        $('#addToCompare').val("true");
        $('#productForm').submit();
    });
});
