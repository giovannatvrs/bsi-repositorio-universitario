const listaDisciplinas = document.getElementById('lista-disciplinas');
const btnDisciplinas = document.getElementById('btn-disciplinas');
const botaoNovoArquivo = document.getElementById('novo');
const popUp = document.getElementById('overlay');
const fechar = document.getElementById('close');
const cancelar = document.getElementById('cancelar');
if(listaDisciplinas){
    
    btnDisciplinas.addEventListener('click', function(){
        listaDisciplinas.classList.toggle('ativa');
    })
}

if(popUp){
    novo.addEventListener('click', function(){
        popUp.classList.toggle('ativo');
    })
    fechar.addEventListener('click', function(){
        popUp.classList.remove('ativo');
    })
    cancelar.addEventListener('click', function(){
        popUp.classList.remove('ativo');
    })

}