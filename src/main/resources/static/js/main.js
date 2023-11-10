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


    function parseDate(date) {
        const utc = new Date(Date.UTC(date.getFullYear(),
            date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()));
        day = utc.getDate()
        month = utc.getMonth() + 1
        year = utc.getFullYear()
        hour = utc.getHours()
        minute = utc.getMinutes()
        seconds = utc.getSeconds()
        result = (day).toString().padStart(2, "0") + "-" +
            (month).toString().padStart(2, "0") + "-" +
            year + " " + " " + (hour).toString().padStart(2, "0") +
            ":" + (minute).toString().padStart(2, "0") + ":" + (seconds).toString().padStart(2, "0")
        return result
    }

    function changeTimeZone(date, timeZone) {
        return new Date(
            date.toLocaleString('en-US', {
                timeZone,
            }),
        );
    }

    let date_time_elements = document.querySelectorAll(".date-time");
    for (var i = 0; i < date_time_elements.length; i++) {
        var element = date_time_elements[i];
        element.textContent = parseDate(changeTimeZone(new Date(element.textContent), "UTC"))
    }

});

function render_md() {
    let content = document.querySelector('#content').value;
    document.querySelector('#md_preview').innerHTML = converter.makeHtml(content)
}
