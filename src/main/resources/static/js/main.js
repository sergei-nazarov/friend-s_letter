var converter;
document.addEventListener('DOMContentLoaded', function () {
    converter = new showdown.Converter();
    $('#content').markdown({
        hiddenButtons: ['cmdPreview', 'fullscreen'],
        resize: 'vertical',
        fullscreen: {enable: false},
        onChange: function (e) {
            render_md();
        }
    });
    let locales = document.querySelector("#locales");
    var children = locales.children;
    for (var i = 0; i < children.length; i++) {
        var child = children[i];
        child.href = '?lang=' + child.getAttribute('value');
    }
    render_md();
    let tz = Intl.DateTimeFormat().resolvedOptions().timeZone
    document.querySelector("#tz").value = tz
    document.querySelector("#TimezoneField").value = tz
});

function render_md() {
    let content = document.querySelector('#content').value;
    document.querySelector('#md_preview').innerHTML = converter.makeHtml(content)
}
