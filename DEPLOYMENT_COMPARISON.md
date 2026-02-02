# 🔄 Comparaison des modes de déploiement

Choisissez le mode qui convient le mieux à votre situation.

---

## 📊 Tableau Comparatif

| Critère | 🌐 Mode Centralisé | 📦 Mode Standalone |
|---------|-------------------|-------------------|
| **Nombre de repos** | 2+ repositories | 1 repository |
| **Temps de setup initial** | 5 min (une fois) | 5 min |
| **Temps par repo additionnel** | 2 min | 5 min |
| **Maintenance** | ✅ Centralisée | ⚠️ Par repo |
| **Mises à jour** | ✅ Automatiques partout | ❌ Manuelles par repo |
| **Personnalisation** | ✅ Par repo (via paramètres) | ✅ Totale |
| **Complexité** | Moyenne | Simple |
| **Recommandé pour** | Organisations, équipes | Projets individuels |

---

## 🌐 Mode Centralisé - Architecture

```
┌──────────────────────────────────────────────────────────┐
│         REPOSITORY CENTRAL                               │
│         github.com/vous/java-ai-code-review              │
│                                                          │
│  📁 .github/workflows/                                   │
│     └── reusable-ai-review.yml  ← Workflow réutilisable │
│  📁 scripts/                                             │
│     ├── ai_code_reviewer.py     ← Script Python         │
│     └── requirements.txt         ← Dépendances          │
│                                                          │
│  📄 Documentation:                                       │
│     ├── CENTRALIZED_QUICK_START.md                      │
│     ├── CENTRALIZED_SETUP.md                            │
│     └── example-caller-workflow.yml                     │
└──────────────────────────────────────────────────────────┘
                          ▲
                          │
                          │ Appelle via "uses:"
                          │
      ┌──────────────┬────┴─────┬──────────────┐
      │              │          │              │
      ▼              ▼          ▼              ▼
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│  Repo A  │  │  Repo B  │  │  Repo C  │  │  Repo D  │
│          │  │          │  │          │  │          │
│ .github/ │  │ .github/ │  │ .github/ │  │ .github/ │
│ workflows│  │ workflows│  │ workflows│  │ workflows│
│   │      │  │   │      │  │   │      │  │   │      │
│   └─ ai- │  │   └─ ai- │  │   └─ ai- │  │   └─ ai- │
│   code-  │  │   code-  │  │   code-  │  │   code-  │
│   review │  │   review │  │   review │  │   review │
│   .yml   │  │   .yml   │  │   .yml   │  │   .yml   │
│  (20     │  │  (20     │  │  (20     │  │  (20     │
│  lignes) │  │  lignes) │  │  lignes) │  │  lignes) │
└──────────┘  └──────────┘  └──────────┘  └──────────┘
```

### ✅ Avantages

1. **Maintenance simplifiée**
   - Modifiez `ai_code_reviewer.py` une fois → tous les repos mis à jour
   - Pas besoin de synchroniser manuellement

2. **Déploiement rapide**
   - Nouveau repo = copier 20 lignes de YAML
   - Pas de copie de scripts Python

3. **Cohérence garantie**
   - Tous les repos utilisent la même version
   - Règles d'analyse identiques partout

4. **Gestion des secrets simplifiée**
   - Secret d'organisation : configurez une fois
   - Sinon : secret par repo (isolation)

### ❌ Inconvénients

1. **Dépendance au repo central**
   - Si le repo central est inaccessible, les workflows échouent
   - Solution : Utiliser un commit SHA spécifique

2. **Courbe d'apprentissage**
   - Concept de "reusable workflow" à comprendre
   - Plus de configuration initiale

---

## 📦 Mode Standalone - Architecture

```
┌──────────────────────────────────────────────────────────┐
│         REPOSITORY DU PROJET                             │
│         github.com/vous/mon-projet-java                  │
│                                                          │
│  📁 src/                          ← Code Java            │
│     └── main/java/...                                    │
│                                                          │
│  📁 .github/                                             │
│     ├── workflows/                                       │
│     │   └── ai-code-review.yml  ← Workflow complet      │
│     └── config/                                          │
│         ├── checkstyle.xml       ← Config Checkstyle    │
│         └── pmd-ruleset.xml      ← Config PMD           │
│                                                          │
│  📁 scripts/                                             │
│     ├── ai_code_reviewer.py     ← Script Python         │
│     └── requirements.txt         ← Dépendances          │
│                                                          │
│  📄 pom.xml ou build.gradle                              │
└──────────────────────────────────────────────────────────┘
```

### ✅ Avantages

1. **Autonomie complète**
   - Pas de dépendance externe
   - Fonctionne même si d'autres repos sont down

2. **Personnalisation totale**
   - Modifiez le script Python directement
   - Adaptez complètement à vos besoins

3. **Simplicité conceptuelle**
   - Tout est dans un seul repo
   - Facile à comprendre et débugger

### ❌ Inconvénients

1. **Maintenance fragmentée**
   - 10 repos = 10 copies du script à maintenir
   - Mises à jour fastidieuses

2. **Divergence possible**
   - Les repos peuvent avoir des versions différentes
   - Incohérence dans les analyses

3. **Duplication**
   - Scripts Python dupliqués partout
   - Occupation d'espace disque

---

## 🎯 Quel mode choisir ?

### Choisissez le Mode Centralisé si :

✅ Vous avez **2+ repositories Java**
✅ Vous travaillez en **équipe/organisation**
✅ Vous voulez **centraliser la maintenance**
✅ Vous voulez **cohérence entre projets**
✅ Vous voulez **déployer rapidement** sur de nouveaux repos

**➡️ [Commencer avec le mode centralisé](CENTRALIZED_QUICK_START.md)**

### Choisissez le Mode Standalone si :

✅ Vous avez **1 seul repository**
✅ Vous êtes **développeur solo**
✅ Vous voulez **contrôle total** sur le code
✅ Vous voulez **personnaliser fortement** le système
✅ Vous préférez l'**autonomie**

**➡️ [Commencer avec le mode standalone](QUICK_START.md)**

---

## 🔄 Peut-on changer de mode ?

### De Standalone → Centralisé

**Facile !** ✅

1. Créez le repo central
2. Remplacez le workflow local par un workflow appelant
3. Supprimez les scripts Python locaux

### De Centralisé → Standalone

**Possible mais moins recommandé**

1. Copiez les scripts du repo central
2. Remplacez le workflow appelant par un workflow complet
3. Maintenez manuellement les copies

---

## 💡 Hybride : Le meilleur des deux mondes

Vous pouvez combiner les approches :

1. **Base centralisée** pour la majorité des repos
2. **Copie standalone** pour 1-2 repos avec besoins spécifiques

**Exemple :**
```
Repos A, B, C, D → Mode centralisé (règles standard)
Repo E → Mode standalone (règles customisées pour le legacy)
```

---

## 📊 Scénarios d'usage

### Scénario 1 : Startup avec 5 microservices

**Recommandation : Mode Centralisé** 🌐

- 5 repos Java Spring Boot
- Équipe de 3-5 développeurs
- Besoin de cohérence
- Déploiement rapide

**Gain de temps : ~15 minutes de maintenance par mois**

---

### Scénario 2 : Projet open source unique

**Recommandation : Mode Standalone** 📦

- 1 seul repository
- Communauté de contributeurs
- Autonomie importante
- Pas de dépendance externe

**Avantage : Contrôle total**

---

### Scénario 3 : Entreprise avec 20+ projets Java

**Recommandation : Mode Centralisé + Organisation Secret** 🌐

- Secret d'organisation : une seule clé API
- Workflow centralisé
- Équipe DevOps gère le repo central
- Équipes produit utilisent simplement

**Gain de temps : ~2 heures par mois**
**Économies : Budget API centralisé**

---

### Scénario 4 : Agence avec projets clients

**Recommandation : Mode Hybride** 🔄

- Repo central pour vos projets internes
- Standalone pour les repos des clients
- Flexibilité maximale

---

## 🚀 Prêt à commencer ?

### Mode Centralisé
- **Quick Start** : [CENTRALIZED_QUICK_START.md](CENTRALIZED_QUICK_START.md) (3 min)
- **Guide complet** : [CENTRALIZED_SETUP.md](CENTRALIZED_SETUP.md) (15 min)

### Mode Standalone
- **Quick Start** : [QUICK_START.md](QUICK_START.md) (5 min)
- **Guide complet** : [README.md](README.md) (20 min)

---

## ❓ Besoin d'aide pour choisir ?

Posez-vous ces questions :

1. **Combien de repositories Java ai-je ?**
   - 1 → Standalone
   - 2+ → Centralisé

2. **Combien de temps puis-je consacrer à la maintenance ?**
   - Peu de temps → Centralisé
   - Beaucoup de temps → Standalone

3. **Ai-je besoin de personnalisations spécifiques par repo ?**
   - Oui → Standalone (ou Centralisé avec paramètres)
   - Non → Centralisé

4. **Travaille-je en équipe ?**
   - Oui → Centralisé (cohérence)
   - Non → Au choix

---

**💡 Astuce :** En cas de doute, commencez avec le **mode centralisé**. Vous pourrez toujours migrer vers standalone si nécessaire, et vous gagnerez du temps dès le départ !
