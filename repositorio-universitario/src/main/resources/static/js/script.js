
const listaDisciplinas = document.getElementById('lista-disciplinas');
const btnDisciplinas = document.getElementById('btn-disciplinas');
const botaoNovoArquivo = document.getElementById('novo');
const popUp = document.getElementById('overlay');
const fechar = document.getElementById('close');
const cancelar = document.getElementById('cancelar');
let paginaAtual = 0;
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



async function fetchData(){

    try{
        const response = await fetch("/usuario-logado",{
            credentials: 'include'
        });

        if(!response.ok){
            if(response.status == 401){
                window.location.href = "/login";
            }
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        const data= await response.json();
        console.log(data);

        atualizarPerfil(data);
        atualizarNavegacao(data)

    }catch(error){
        console.error("Falha em carregar os dados do usuário",error);

    }


}

function atualizarPerfil(data){
    const fotoDePerfil = document.getElementById("foto-perfil");
    const nomeUsuario = document.getElementById("nome-usuario");
    if(fotoDePerfil) {
        fotoDePerfil.src = data.picture || "default.png";
    }
    if(nomeUsuario){
        nomeUsuario.textContent = data.name;
    }
}

document.addEventListener("DOMContentLoaded", fetchData);



function atualizarNavegacao(data){
    var urlData= window.location.pathname;
    const paginas = document.getElementById("paginas");
    if (urlData == "/usuario.html"){
        if(data.funcao=="ADMINISTRADOR"){
            paginas.innerHTML = `<a>Histórico</a>
            <a href="envios.html">Envios</a>
            <a href="/usuarios.html">Usuários</a>
            <a href="/moderadores.html">Moderadores</a>`
        }
        else if (data.funcao == "MODERADOR"){
            paginas.innerHTML = `<a href="envios.html">Envios</a>
            <a href="/solicitacoes.html">Solicitações</a>
            <a href="restricoes.html">Restrições</a>`
        }
        else{
            paginas.innerHTML = `<a href="envios.html">Envios</a>`
        }

    }

}





function reprovarArquivo(){
    document.querySelectorAll(".reprovar-arquivo").forEach(icon => {
        icon.addEventListener('click', async()=>{
            const fileId = icon.getAttribute('data-id');
            try{
                const response = await fetch(`/reprovar/${fileId}`, {
                    method: 'PUT',
                    credentials: "include"
                })
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Arquivo reprovado");
                window.location.reload();
            }catch(error){
                console.error("Falha em reprovar arquivo", error);
            }
        });
    });
}

function aprovarArquivo(){
    document.querySelectorAll(".aprovar-arquivo").forEach(icon =>{
        icon.addEventListener('click', async() => {
            const fileId = icon.getAttribute('data-id');
            try{
                const response = await fetch(`/aprovar/${fileId}`, {
                    method: 'PUT',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Arquivo aprovado com sucesso!");
                window.location.reload();
            }catch(error){
                console.error("Falha em aprovar arquivo", error);
            }
        });
    });
}

function deletarArquivo(){
    document.querySelectorAll(".deletar-arquivo").forEach(icon => {
        icon.addEventListener('click', async() =>{
            const fileId = icon.getAttribute('data-id');
            try{
                const response = await fetch(`/deletar/${fileId}`,{
                    method:'DELETE',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Arquivo deletado!");
                window.location.reload();
            }catch(error){
                console.error("Falha em deletar arquivo", error);
            }

        })
        
    })
}

function downloadArquivo(){
    document.querySelectorAll(".botao-download").forEach(icon =>{
        icon.addEventListener('click', async() => {
            const fileId = icon.getAttribute('data-id');
            const fileName = icon.getAttribute('data-nome');
            try{
                const response = await fetch(`/download/${fileId}`, {
                    method: 'GET',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = fileName || 'arquivo';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
            }catch (error){
                console.error("Falha em realizar o download", error);
            }
        })
    })
}

function visualizarArquivo(){
    document.querySelectorAll(".botao-visualizar").forEach(icon =>{
        icon.addEventListener('click', async() => {
            const fileId = icon.getAttribute('data-id');
            window.open(`/visualizar/${fileId}`, '_blank');
        });
    });
}



function suspenderUsuario(){
    document.querySelectorAll(".botao-suspender-usuario").forEach(button => {
        button.addEventListener('click', async () =>{
            const userId = button.getAttribute('data-id');
            try{
                const response = await fetch(`/suspender/${userId}`, {
                    method: 'PUT',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Usuário(a) suspenso");
                window.location.reload();

            }catch(error){
                console.error("Falha em suspender usuário", error);
            }
        })
    })
}

function retirarSuspensao(){
    document.querySelectorAll(".retirar-suspensao").forEach(button => {
        button.addEventListener('click', async() =>{
            const userId = button.getAttribute('data-id');
            try{
                const response = await fetch(`/retirar-suspensao/${userId}`, {
                    method: 'PUT',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Suspensão retirada");
                window.location.reload();

            }catch(error){
                console.error("Falha em retirar a suspensão do usuário", error);
            }
        })
    })
}








function ativarPromocaoUsuarios(){
    document.querySelectorAll('.botao-tornar-usuario-moderador').forEach(button =>{
        button.addEventListener('click', async () =>{
            const userId = button.getAttribute('data-id');
            try{
                const response = await fetch(`/promover/${userId}`, {
                    method: "PUT",
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Usuário(a) promovido com sucesso");
                window.location.reload();
            }catch(error){
                console.error("Falha em promover usuário para moderador", error);
            }
        });
    });
}

function ativarDesligamentoModerador(){
    document.querySelectorAll('.botao-retirar-papel-moderador').forEach(button =>{
        button.addEventListener('click', async () =>{
            const userId = button.getAttribute('data-id');
            try{
                const response = await fetch(`/retirar-papel/${userId}`, {
                   method: 'PUT',
                   credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Papel de moderador retirado do usuário com sucesso");
                window.location.reload();
            }catch(error){
                console.error("Erro em retirar papel do moderador", error);
            }
        })
    });
}



function fazerUploadArquivo(){
    const upload = document.getElementById("confirmar");
    upload.addEventListener("click", async () =>{
        try{
            let arquivo = document.getElementById("arquivo");
            let nomeArquivo = document.getElementById("nome-arquivo");
            let disciplina = document.getElementById("disciplina");
            let descricao = document.getElementById("descricao-arquivo");

            const formData = new FormData;
            formData.append("file", arquivo.files[0]);
            formData.append("nome", nomeArquivo.value);
            formData.append("disciplina", disciplina.value);
            formData.append("descricao", descricao.value);
            const response = await fetch(`/upload`, {
               method: 'POST',
                credentials: "include",
                body: formData
            });
            console.log(response.status);
            if(response.status === 401){
                alert("Não é possível realizar upload. Motivo: sua conta está suspensa");
                window.location.reload();
            }
            else if(!response.ok){
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            else {
                arquivo.value = '';
                nomeArquivo.value = '';
                disciplina.value = '';
                descricao.value = '';
                alert("Arquivo enviado para avaliação");
                window.location.reload();
            }

        }catch (error){
            console.error("Erro em realizar upload de arquivo", error);
        }
    });
}

fazerUploadArquivo();