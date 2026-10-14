
const listaDisciplinas = document.getElementById('lista-disciplinas');
const btnDisciplinas = document.querySelector('.filtro-disciplina');
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

const seta = document.querySelector('#seta-mais-opcoes');
const opcoes = document.querySelector('.opcoes');

seta.addEventListener('click', () => {
  opcoes.classList.toggle('visivel');
});



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




function atualizarNavegacao(data) {
    const paginas = document.getElementById("paginas");
    if (!paginas) return;

    const funcao = data.funcao;
    const caminhoAtual = window.location.pathname;

    let links = [];
    if (caminhoAtual !== "/home.html") {
        links.push({
            texto: "Início",
            href: "/home.html"
        });
    }

    if (funcao === "ADMINISTRADOR") {
        links.push(
            { texto: "Histórico", href: "/historico.html" },
            { texto: "Envios", href: "/envios.html" },
            { texto: "Usuários", href: "/usuarios.html" },
            { texto: "Moderadores", href: "/moderadores.html" }
        );
    } else if (funcao === "MODERADOR") {
        links.push(
            { texto: "Histórico", href: "/historico.html" },
            { texto: "Envios", href: "/envios.html" },
            { texto: "Solicitações", href: "/solicitacoes.html" },
            { texto: "Restrições", href: "/restricoes.html" }
        );
    } else {
        links.push(
            { texto: "Histórico", href: "/historico.html" },
            { texto: "Envios", href: "/envios.html" }
        );
    }

    paginas.innerHTML = links.map(link => {
        const ativa = caminhoAtual === link.href;

        return `
            <a href="${link.href}"
               class="${ativa ? "ativa" : ""}"
               ${ativa ? 'aria-current="page"' : ""}>
                ${link.texto}
            </a>
        `;
    }).join("");
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

function alternarDescricao(id) {
    const trDescricao = document.getElementById(`descricao-${id}`);
    const iconeSeta = document.getElementById(`seta-${id}`);

    if (trDescricao) {
        if (trDescricao.style.display === "none") {
            trDescricao.style.display = "table-row";
            if (iconeSeta) iconeSeta.style.transform = "rotate(90deg)";
        } else {
            trDescricao.style.display = "none";
            if (iconeSeta) iconeSeta.style.transform = "rotate(0deg)";
        }
    }
}