document.addEventListener('DOMContentLoaded', function () {
    let locales = document.querySelector("#locales");
    var children = locales.children;
    for (var i = 0; i < children.length; i++) {
        var child = children[i];
        child.href = '?lang=' + child.getAttribute('value');
    }
});
