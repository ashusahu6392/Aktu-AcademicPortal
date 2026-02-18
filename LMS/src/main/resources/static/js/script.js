
document.addEventListener('DOMContentLoaded', function(){

    // Sidebar active highlight
    document.querySelectorAll('.subject-link, .unit-link, .topic-link')
        .forEach(function(el){
            el.addEventListener('click', function(){
                var cls = el.classList.contains('subject-link') 
                    ? 'subject-link' 
                    : (el.classList.contains('unit-link') 
                        ? 'unit-link' 
                        : 'topic-link');

                document.querySelectorAll('.' + cls)
                    .forEach(function(e){ e.classList.remove('active'); });

                el.classList.add('active');
            });
        });

    // Sidebar toggle
    var sidebar = document.querySelector('.sidebar-inner');
    if(!sidebar) return;

    var btn = document.createElement('button');
    btn.className = 'btn btn-sm btn-outline-secondary d-md-none mb-3';
    btn.textContent = 'Toggle Subjects';

    btn.addEventListener('click', function(){
        sidebar.classList.toggle('collapsed');
        sidebar.style.display = sidebar.classList.contains('collapsed') ? 'none' : '';
    });

    sidebar.parentNode.insertBefore(btn, sidebar);

});
