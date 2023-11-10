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
    document.querySelector("#tz").textContent = tz


    function changeTimeZone(date, timeZone) {
        return new Date(
            date.toLocaleString('en-US', {
                timeZone,
            }),
        );
    }

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


    let date_time_elements = document.querySelectorAll(".date-time");
    for (var i = 0; i < date_time_elements.length; i++) {
        var element = date_time_elements[i];
        var date = parseDate(changeTimeZone(new Date(element.textContent)))
        element.textContent = date
    }

    let copyButton = document.getElementById('copyButton');
    if (copyButton != null) {
        copyButton.addEventListener('click', copyToBuffer)
    }

    function copyToBuffer() {
        copyToClipboard(document.getElementById("letterUrl").textContent);
    }

    async function copyToClipboard(textToCopy) {
        // Navigator clipboard api needs a secure context (https)
        if (navigator.clipboard && window.isSecureContext) {
            await navigator.clipboard.writeText(textToCopy);
        } else {
            // Use the 'out of viewport hidden text area' trick
            const textArea = document.createElement("textarea");
            textArea.value = textToCopy;

            // Move textarea out of the viewport so it's not visible
            textArea.style.position = "absolute";
            textArea.style.left = "-999999px";

            document.body.prepend(textArea);
            textArea.select();

            try {
                document.execCommand('copy');
            } catch (error) {
                console.error(error);
            } finally {
                textArea.remove();
            }
        }
    }

});

function render_md() {
    let element = document.querySelector('#md_preview');
    element.innerHTML = converter.makeHtml(element.textContent)
}
